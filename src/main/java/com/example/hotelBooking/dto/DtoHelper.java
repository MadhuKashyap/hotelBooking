package com.example.hotelBooking.dto;

import com.example.hotelBooking.dao.HotelDao;
import com.example.hotelBooking.dao.RoomDao;
import com.example.hotelBooking.dao.UserDao;
import com.example.hotelBooking.form.UserForm;
import com.example.hotelBooking.model.data.BookingHistory;
import com.example.hotelBooking.model.data.HotelData;
import com.example.hotelBooking.model.data.RoomData;
import com.example.hotelBooking.model.enums.BookingStatus;
import com.example.hotelBooking.model.enums.UserType;
import com.example.hotelBooking.model.form.BookingForm;
import com.example.hotelBooking.pojo.BookingHistoryPojo;
import com.example.hotelBooking.pojo.HotelPojo;
import com.example.hotelBooking.form.HotelFilterForm;
import com.example.hotelBooking.pojo.RoomPojo;
import com.example.hotelBooking.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DtoHelper {
    @Autowired
    private RoomDao roomDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private HotelDao hotelDao;

    public boolean validateUserForm(UserForm userForm) {
        if (userForm == null) return false;
        if (userForm.getName() == null || userForm.getName().trim().isEmpty()) return false;
        if (userForm.getEmail() == null || userForm.getEmail().trim().isEmpty()) return false;
        if (!userForm.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) return false;
        if (userForm.getPassword() == null || userForm.getPassword().trim().isEmpty()) return false;
        if (userForm.getPhone() == null || userForm.getPhone().trim().isEmpty()) return false;
        if (userForm.getRole() == null || userForm.getRole().trim().isEmpty()) return false;
        // Optionally check addressId or address if required
        return true;
    }
    public List<HotelPojo> filteHotelByDateRange(List<HotelPojo> hotels, HotelFilterForm filterForm) {
        Date startDate = filterForm.getStartDate();
        Date endDate = filterForm.getEndDate();
        if (startDate == null || endDate == null) {
            return hotels;
        }
        List<HotelPojo> filtered = new ArrayList<>();
        for (HotelPojo hotel : hotels) {
            boolean hasAvailableRoom = false;
            List<RoomPojo> rooms = roomDao.findByHotelId(hotel.getId()); // Assumes HotelPojo has getRooms()
            if (rooms != null) {
                for (RoomPojo room : rooms) {
                    List<Date> bookedDates = room.getBookedDates(); // Assumes RoomPojo has getBookedDates()
                    boolean isAvailable = true;
                    if (bookedDates != null) {
                        for (Date booked : bookedDates) {
                            if (!booked.before(startDate) && !booked.after(endDate)) {
                                isAvailable = false;
                                break;
                            }
                        }
                    }
                    if (isAvailable) {
                        hasAvailableRoom = true;
                        break;
                    }
                }
            }
            if (hasAvailableRoom) {
                filtered.add(hotel);
            }
        }
        return filtered;
    }

    public List<HotelPojo> filterHotelByPrice(List<HotelPojo> hotels, HotelFilterForm filterForm) {
        //TODO : Implement this
        return new ArrayList<>();
    }

    public static List<HotelPojo> filterHotelByRating(List<HotelPojo> hotels, HotelFilterForm filterForm) {
        Integer ratings = filterForm.getRatings();
        if (ratings == null) return hotels;
        return hotels.stream()
                .filter(h -> h.getRating() >= ratings)
                .collect(Collectors.toList());
    }
    public UserPojo mapToPojo(UserForm userForm) {
        UserPojo pojo = new UserPojo();
        pojo.setName(userForm.getName());
        pojo.setEmail(userForm.getEmail());
        pojo.setPassword(userForm.getPassword());
        pojo.setPhone(userForm.getPhone());
        pojo.setAddress(userForm.getAddress());
        pojo.setRole(UserType.valueOf(userForm.getRole()));
        pojo.setAddressId(userForm.getAddressId());
        return pojo;
    }
    public HotelData convertHotelPojoToData(HotelPojo pojo) {
        HotelData data = new HotelData();
        data.setId(pojo.getId());
        data.setName(pojo.getName());
        data.setDescription(pojo.getDescription());
        data.setRating(pojo.getRating() != null ? pojo.getRating() : null);
        data.setAddressId(pojo.getAddressId());
        return data;
    }
    public RoomData convertRoomPojoToData(RoomPojo pojo) {
        RoomData data = new RoomData();
        data.setId(pojo.getId());
        data.setAmenities(pojo.getAmenities());
        data.setRoomNumber(pojo.getRoomNumber());
        data.setRoomType(pojo.getRoomType());
        data.setPrice(pojo.getPrice());
        // Add more mappings if HotelData has more fields
        return data;
    }

    public BookingHistoryPojo saveBookingHistory(RoomPojo roomPojo,
                                                 BookingHistoryPojo historyPojo,
                                                 BookingForm bookingForm,
                                                 UserPojo userPojo) {
        historyPojo.setUserId(userPojo.getId());
        historyPojo.setRoomId(roomPojo.getId());
        historyPojo.setStartDate(historyPojo.getStartDate());
        historyPojo.setPriceTotal(roomPojo.getPrice());
        historyPojo.setStartDate(bookingForm.getStartDate());
        historyPojo.setEndDate(bookingForm.getEndDate());
        historyPojo.setStatus(BookingStatus.PAYMENT_PENDING);
        return historyPojo;
    }

    public BookingHistory convertBookingHistoryPojoToData(BookingHistoryPojo bookingHistoryPojo) {
        BookingHistory bookingHistory = new BookingHistory();
        
        // Set basic booking information
        bookingHistory.setBookingId(bookingHistoryPojo.getId());
        bookingHistory.setStartDate(bookingHistoryPojo.getStartDate());
        bookingHistory.setEndDate(bookingHistoryPojo.getEndDate());
        bookingHistory.setPriceTotal(bookingHistoryPojo.getPriceTotal());
        
        // Fetch and set user information
        Optional<UserPojo> userOptional = userDao.findById(bookingHistoryPojo.getUserId());
        if (userOptional.isPresent()) {
            UserPojo user = userOptional.get();
            bookingHistory.setUsername(user.getId()); // Using ID as username since field is Long
            bookingHistory.setUserEmail(user.getId()); // Using ID as email since field is Long
        }
        
        // Fetch and set room and hotel information
        Optional<RoomPojo> roomOptional = roomDao.findById(bookingHistoryPojo.getRoomId());
        if (roomOptional.isPresent()) {
            RoomPojo room = roomOptional.get();
            bookingHistory.setRoomName("Room " + room.getRoomNumber());
            bookingHistory.setRoomType(room.getRoomType().toString());
            bookingHistory.setRoomDescription("Room type: " + room.getRoomType() + ", Price: " + room.getPrice());
            
            // Fetch hotel information
            Optional<HotelPojo> hotelOptional = hotelDao.findById(room.getHotelId());
            if (hotelOptional.isPresent()) {
                HotelPojo hotel = hotelOptional.get();
                bookingHistory.setHotelName(hotel.getName());
            }
        }
        
        return bookingHistory;
    }
}
