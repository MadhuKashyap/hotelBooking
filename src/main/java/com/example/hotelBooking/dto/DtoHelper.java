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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DtoHelper {
    @Autowired
    private RoomDao roomDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private HotelDao hotelDao;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    ObjectMapper mapper = new ObjectMapper();

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
    public List<HotelPojo> filterHotelByDateRange(List<HotelPojo> hotels, HotelFilterForm filterForm) throws JsonProcessingException {
        String startDate = filterForm.getStartDate();
        String endDate = filterForm.getEndDate();
        if (startDate == null || endDate == null) {
            return hotels;
        }
        List<HotelPojo> filtered = new ArrayList<>();
        for (HotelPojo hotel : hotels) {
            boolean hasAvailableRoom = false;
            List<RoomPojo> rooms = roomDao.findByHotelId(hotel.getId()); // Assumes HotelPojo has getRooms()
            if (rooms != null) {
                for (RoomPojo room : rooms) {
                    String bookedDatesString = room.getBookedDates();
                    List<String> bookedDates =  mapper.readValue(bookedDatesString, new TypeReference<List<String>>() {});
                    boolean isAvailable = true;
                    if (bookedDates != null) {
                        for (String booked : bookedDates) {
                            if (!LocalDate.parse(booked).isBefore(LocalDate.parse(startDate)) &&
                                    !LocalDate.parse(booked).isAfter(LocalDate.parse(endDate))) {
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
        Double priceStart = filterForm.getPriceStart();
        Double priceEnd = filterForm.getPriceEnd();
        List<HotelPojo> filtered = new ArrayList<>();
        for (HotelPojo hotel : hotels) {
            List<RoomPojo> rooms = roomDao.findByHotelId(hotel.getId());
            boolean hasRoomInRange = false;
            for (RoomPojo room : rooms) {
                double price = room.getPrice();
                if ((priceStart == null || price >= priceStart) &&
                    (priceEnd == null || price <= priceEnd)) {
                    hasRoomInRange = true;
                    break;
                }
            }
            if (hasRoomInRange) {
                filtered.add(hotel);
            }
        }
        return filtered;
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
    public RoomData convertRoomPojoToData(RoomPojo pojo) throws JsonProcessingException {
        RoomData data = new RoomData();
        data.setId(pojo.getId());
        data.setAmenities(mapper.readValue(pojo.getAmenities(), new TypeReference<List<String>>() {}));
        data.setRoomNumber(pojo.getRoomNumber());
        data.setRoomType(pojo.getRoomType());
        data.setPrice(pojo.getPrice());
        data.setBookedDates(mapper.readValue(pojo.getBookedDates(), new TypeReference<List<String>>() {}));
        return data;
    }

    public BookingHistoryPojo saveBookingHistory(RoomPojo roomPojo,
                                                 BookingHistoryPojo historyPojo,
                                                 BookingForm bookingForm,
                                                 UserPojo userPojo) throws ParseException {
        historyPojo.setUserId(userPojo.getUserId());
        historyPojo.setRoomId(roomPojo.getId());
        historyPojo.setStartDate(historyPojo.getStartDate());
        historyPojo.setPriceTotal(roomPojo.getPrice());
        historyPojo.setStartDate(formatter.parse(bookingForm.getStartDate()));
        historyPojo.setEndDate(formatter.parse(bookingForm.getEndDate()));
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
        UserPojo user = userDao.findByUserId(bookingHistoryPojo.getUserId());
        bookingHistory.setUsername(user.getId()); // Using ID as username since field is Long
        bookingHistory.setUserEmail(user.getId()); // Using ID as email since field is Long
        
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

    public void cancelRoomAndRemoveDates(RoomPojo roomPojo, String startDateStr, String endDateStr) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        // Parse booked dates from JSON string
        List<String> bookedDates = mapper.readValue(roomPojo.getBookedDates(), new TypeReference<List<String>>() {});
        Iterator<String> iterator = bookedDates.iterator();

        while (iterator.hasNext()) {
            LocalDate booked = LocalDate.parse(iterator.next());
            if ((booked.isEqual(startDate) || booked.isAfter(startDate)) &&
                    (booked.isEqual(endDate) || booked.isBefore(endDate))) {
                iterator.remove();
            }
        }

        // Convert back to JSON and set on roomPojo
        roomPojo.setBookedDates(mapper.writeValueAsString(bookedDates));
    }

    public boolean callDummyPaymentService() {
        return true;
    }
}
