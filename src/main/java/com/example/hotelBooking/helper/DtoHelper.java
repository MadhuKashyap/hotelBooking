package com.example.hotelBooking.helper;

import com.example.hotelBooking.pojo.UserPojo;
import com.example.hotelBooking.form.UserForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.hotelBooking.pojo.HotelPojo;
import com.example.hotelBooking.pojo.RoomPojo;

import java.util.*;
import com.example.hotelBooking.model.form.*;
import com.example.hotelBooking.dao.RoomDao;
import java.util.stream.Collectors;

@Component
public class DtoHelper {
    @Autowired
    private RoomDao roomDao;
    public static boolean validateUser(UserPojo userPojo) {
        // Add validation logic here (e.g., check for nulls, email format, etc.)
        if (userPojo.getName() == null || userPojo.getEmail() == null || userPojo.getPassword() == null) {
            return false;
        }
        // Add more validation as needed
        return true;
    }

    public static boolean validateUserForm(UserForm userForm) {
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
    public static List<HotelPojo> filteHotelByDateRange(List<HotelPojo> hotels, HotelFilterForm filterForm) {
        // Placeholder: implement actual date range filtering logic if HotelPojo has date fields
        // For now, return the original list (since HotelPojo has no date fields)
        return hotels;
    }

    public List<HotelPojo> filterHotelByPrice(List<HotelPojo> hotels, HotelFilterForm filterForm) {
        Double priceStart = filterForm.getPriceStart();
        Double priceEnd = filterForm.getPriceEnd();
        if (priceStart == null && priceEnd == null) return hotels;
        return hotels.stream()
                .filter(hotel -> {
                    List<RoomPojo> rooms = roomDao.findByHotelId(hotel.getId());
                    return rooms.stream().anyMatch(room -> {
                        double price = room.getPrice();
                        boolean withinStart = (priceStart == null || price >= priceStart);
                        boolean withinEnd = (priceEnd == null || price <= priceEnd);
                        return withinStart && withinEnd;
                    });
                })
                .collect(Collectors.toList());
    }

    public static List<HotelPojo> filterHotelByRating(List<HotelPojo> hotels, HotelFilterForm filterForm) {
        Integer ratings = filterForm.getRatings();
        if (ratings == null) return hotels;
        return hotels.stream().filter(x -> x.getRating() >= ratings)
                .collect(Collectors.toList());
    }

} 