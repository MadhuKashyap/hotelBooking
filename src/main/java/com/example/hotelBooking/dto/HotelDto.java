package com.example.hotelBooking.dto;

import com.example.hotelBooking.dao.BookingHistoryDao;
import com.example.hotelBooking.dao.HotelDao;
import com.example.hotelBooking.dao.RoomDao;
import com.example.hotelBooking.dao.UserDao;
import com.example.hotelBooking.form.HotelFilterForm;
import com.example.hotelBooking.form.UserForm;
import com.example.hotelBooking.model.data.BookingHistory;
import com.example.hotelBooking.model.data.BookingHistoryData;
import com.example.hotelBooking.model.data.HotelData;
import com.example.hotelBooking.model.data.RoomData;
import com.example.hotelBooking.model.enums.BookingStatus;
import com.example.hotelBooking.model.form.BookingForm;
import com.example.hotelBooking.pojo.BookingHistoryPojo;
import com.example.hotelBooking.pojo.HotelPojo;
import com.example.hotelBooking.pojo.RoomPojo;
import com.example.hotelBooking.pojo.UserPojo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HotelDto {
    @Autowired
    private HotelDao hotelDao;

    @Autowired
    private BookingHistoryDao historyDao;

    @Autowired
    private DtoHelper dtoHelper;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private UserDao userDao;

    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    ObjectMapper mapper = new ObjectMapper();
    Calendar cal = Calendar.getInstance();

    public List<HotelData> fetchHotels(HotelFilterForm filterForm) throws JsonProcessingException {
        List<HotelPojo> hotels = hotelDao.findAll();
        List<HotelData> hotelDataList = new ArrayList<>();
        if(!ObjectUtils.isEmpty(filterForm.getStartDate()) && !ObjectUtils.isEmpty(filterForm.getEndDate())) {
            hotels = dtoHelper.filterHotelByDateRange(hotels, filterForm);
        } if(!ObjectUtils.isEmpty(filterForm.getPriceStart()) && !ObjectUtils.isEmpty(filterForm.getPriceEnd())) {
            hotels = dtoHelper.filterHotelByPrice(hotels, filterForm);
        } if(!ObjectUtils.isEmpty(filterForm.getRatings())) {
            hotels = DtoHelper.filterHotelByRating(hotels, filterForm);
        }
        for(HotelPojo hotelPojo : hotels) {
            hotelDataList.add(dtoHelper.convertHotelPojoToData(hotelPojo));
        }
        return hotelDataList;
    }

    public List<RoomData> fetchRoomsByHotelId(Long hotelId) throws JsonProcessingException {
        List<RoomData> roomDataList = new ArrayList<>();
        List<RoomPojo> roomPojoList =  roomDao.findByHotelId(hotelId);
        for(RoomPojo roomPojo : roomPojoList) {
            roomDataList.add(dtoHelper.convertRoomPojoToData(roomPojo));
        }
        return roomDataList;
    }

    @Transactional(rollbackOn = Exception.class)
    public String bookRoom(BookingForm bookingForm) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                userId = userDetails.getUsername();
            } else if (principal instanceof String) {
                userId = (String) principal;
            }
        }
        if (userId == null) {
            throw new Exception("User not authenticated");
        }
        UserPojo userPojo = userDao.findByUserId(userId); //TODO : fetch user here from security context
        for(int retries = 0; retries < 3; retries++) {
            try {
                RoomPojo roomPojo = roomDao.findById(bookingForm.getRoomId()).orElseThrow();
                String bookedDatesString = roomPojo.getBookedDates();
                List<String> bookedDates = mapper.readValue(bookedDatesString, new TypeReference<List<String>>() {});
                for(String date : bookedDates) {
                    if(formatter.parse(date).after(formatter.parse(bookingForm.getStartDate())) &&
                            formatter.parse(date).before(formatter.parse(bookingForm.getEndDate())))
                        throw new Exception("Room already booked");
                }
                String startDate = bookingForm.getStartDate();
                String endDate = bookingForm.getEndDate();
                cal.setTime(formatter.parse(endDate));
                cal.add(Calendar.DATE, 1);
                endDate = formatter.format(cal.getTime());
                while(!startDate.equals(endDate)) {
                    bookedDates.add(startDate);
                    cal.setTime(formatter.parse(startDate));
                    cal.add(Calendar.DATE, 1);
                    startDate = formatter.format(cal.getTime());
                }
                roomPojo.setBookedDates(mapper.writeValueAsString(bookedDates));
                roomDao.save(roomPojo);
                BookingHistoryPojo historyPojo = new BookingHistoryPojo();
                historyPojo = dtoHelper.saveBookingHistory(roomPojo, historyPojo, bookingForm, userPojo);
                historyDao.save(historyPojo);
                return "Room is booked successfully";
            } catch (OptimisticLockException e) {
                if(retries == 2) return "Room is concurrently being booked by another user";
            }
        }
        return "Room not found";
    }

    @Transactional(rollbackOn =
    Exception.class)
    public String cancelRoom(Long bookingId) throws Exception {
        Optional<BookingHistoryPojo> pojo = historyDao.findById(bookingId);
        //do not only change booking status but also remove dates from room pojo
        if(pojo.isPresent()) {
            pojo.get().setStatus(BookingStatus.CANCELLED);
            return "Booking : " + bookingId + " cancelled successfully";
        } else
            throw new Exception("No such booking found");
    }

    public BookingHistoryData viewBookingHistory(UserForm userForm) {
        BookingHistoryData bookingHistoryData = new BookingHistoryData();
        List<BookingHistory> bookingHistories = new ArrayList<>();
        List<BookingHistoryPojo> bookingHistoryPojoList = historyDao.findByUserId(
                userDao.findByUserIdAndPassword(userForm.getUserId(), userForm.getPassword()).getId());
        for(BookingHistoryPojo bookingHistoryPojo : bookingHistoryPojoList) {
            bookingHistories.add(dtoHelper.convertBookingHistoryPojoToData(bookingHistoryPojo));
        }
        bookingHistoryData.setUserHistory(bookingHistories);
        return bookingHistoryData;
    }
}
