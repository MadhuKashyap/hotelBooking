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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import  org.springframework.data.domain.Pageable;
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

    public Page<HotelData> fetchHotels(HotelFilterForm filterForm, int page, int size) throws JsonProcessingException {
        List<HotelPojo> hotels = hotelDao.findAll();
        if(!ObjectUtils.isEmpty(filterForm.getStartDate()) && !ObjectUtils.isEmpty(filterForm.getEndDate())) {
            hotels = dtoHelper.filterHotelByDateRange(hotels, filterForm);
        } if(!ObjectUtils.isEmpty(filterForm.getPriceStart()) && !ObjectUtils.isEmpty(filterForm.getPriceEnd())) {
            hotels = dtoHelper.filterHotelByPrice(hotels, filterForm);
        } if(!ObjectUtils.isEmpty(filterForm.getRatings())) {
            hotels = DtoHelper.filterHotelByRating(hotels, filterForm);
        }
        int start = Math.min(page * size, hotels.size());
        int end = Math.min(start + size, hotels.size());
        List<HotelData> hotelDataList = new ArrayList<>();
        for(HotelPojo hotelPojo : hotels.subList(start, end)) {
            hotelDataList.add(dtoHelper.convertHotelPojoToData(hotelPojo));
        }
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(hotelDataList, pageable, hotels.size());
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
                boolean paymentSuccess = dtoHelper.callDummyPaymentService();
                if (paymentSuccess) {
                    historyPojo.setStatus(BookingStatus.BOOKED);
                    historyDao.save(historyPojo); // Update status to CONFIRMED
                }
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
            Optional<RoomPojo> roomPojo = roomDao.findById(pojo.get().getRoomId());
            if(roomPojo.isPresent()) {
                pojo.get().setStatus(BookingStatus.CANCELLED);
                dtoHelper.cancelRoomAndRemoveDates(roomPojo.get(),
                        formatter.format(pojo.get().getStartDate()),
                        formatter.format(pojo.get().getEndDate()));
                return "Booking : " + bookingId + " cancelled successfully";
            } else {
                throw new Exception("Could not cancel booking, roomId not mapped to this booking");
            }
        } else
            throw new Exception("No such booking found");
    }

    public BookingHistoryData viewBookingHistory(UserForm userForm) {
        BookingHistoryData bookingHistoryData = new BookingHistoryData();
        List<BookingHistory> bookingHistories = new ArrayList<>();
        List<BookingHistoryPojo> bookingHistoryPojoList = historyDao.findByUserId(
                userDao.findByUserIdAndPassword(userForm.getUserId(), userForm.getPassword()).getUserId());
        for(BookingHistoryPojo bookingHistoryPojo : bookingHistoryPojoList) {
            bookingHistories.add(dtoHelper.convertBookingHistoryPojoToData(bookingHistoryPojo));
        }
        bookingHistoryData.setUserHistory(bookingHistories);
        return bookingHistoryData;
    }
}
