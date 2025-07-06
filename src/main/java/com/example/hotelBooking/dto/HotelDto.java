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
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public List<HotelData> fetchHotels(HotelFilterForm filterForm) {
        List<HotelPojo> hotels = hotelDao.findAll();
        List<HotelPojo> hotelByDateRange = new ArrayList<>();
        List<HotelPojo> hotelByPrice = new ArrayList<>();
        List<HotelPojo> hotelByRating = new ArrayList<>();
        List<HotelData> hotelDataList = new ArrayList<>();

        if(!ObjectUtils.isEmpty(filterForm.getStartDate()) && !ObjectUtils.isEmpty(filterForm.getEndDate())) {
            hotelByDateRange = dtoHelper.filteHotelByDateRange(hotels, filterForm);
        } if(!CollectionUtils.isEmpty(hotelByDateRange)) {
            hotelByPrice = dtoHelper.filterHotelByPrice(hotelByDateRange, filterForm);
        } if(!CollectionUtils.isEmpty(hotelByPrice)) {
            hotelByRating = DtoHelper.filterHotelByRating(hotelByPrice, filterForm);
        }
        for(HotelPojo hotelPojo : hotelByRating) {
            hotelDataList.add(dtoHelper.convertHotelPojoToData(hotelPojo));
        }
        return hotelDataList;
    }

    public List<RoomData> fetchRoomsByHotelId(Long hotelId) {
        List<RoomData> roomDataList = new ArrayList<>();
        List<RoomPojo> roomPojoList =  roomDao.findByHotelId(hotelId)
                .stream().filter( x -> x.getBookedDates().size() != 365)
                .toList();
        for(RoomPojo roomPojo : roomPojoList) {
            roomDataList.add(dtoHelper.convertRoomPojoToData(roomPojo));
        }
        return roomDataList;
    }

    @Transactional(rollbackOn = Exception.class)
    public String bookRoom(BookingForm bookingForm) throws Exception {
        UserPojo userPojo = new UserPojo(); //TODO : fetch user here from security context
        for(int retries = 0; retries < 3; retries++) {
            try {
                RoomPojo roomPojo = roomDao.findById(bookingForm.getRoomId()).orElseThrow();
                Date date = new Date();
                if(roomPojo.getBookedDates().contains(date))
                    throw new Exception("Room already booked");
                roomPojo.getBookedDates().add(date);
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
                userDao.findByUserIdPassword(userForm.getUserId(), userForm.getPassword()));
        for(BookingHistoryPojo bookingHistoryPojo : bookingHistoryPojoList) {
            bookingHistories.add(dtoHelper.convertBookingHistoryPojoToData(bookingHistoryPojo));
        }
        bookingHistoryData.setUserHistory(bookingHistories);
        return bookingHistoryData;
    }
}
