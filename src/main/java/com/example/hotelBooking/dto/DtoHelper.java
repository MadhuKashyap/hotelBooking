package com.example.hotelBooking.dto;

import com.example.hotelBooking.dao.RoomDao;
import com.example.hotelBooking.form.UserForm;
import com.example.hotelBooking.model.data.HotelData;
import com.example.hotelBooking.model.data.RoomData;
import com.example.hotelBooking.pojo.HotelPojo;
import com.example.hotelBooking.form.HotelFilterForm;
import com.example.hotelBooking.pojo.RoomPojo;
import com.example.hotelBooking.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoHelper {
    @Autowired
    private RoomDao roomDao;
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
    public static List<HotelPojo> filteHotelByDateRange(List<HotelPojo> hotels, HotelFilterForm filterForm) {
        // Placeholder: implement actual date range filtering logic if HotelPojo has date fields
        // For now, return the original list (since HotelPojo has no date fields)
        return hotels;
    }

    public List<HotelPojo> filterHotelByPrice(List<HotelPojo> hotels, HotelFilterForm filterForm) {
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
        pojo.setRole(userForm.getRole());
        pojo.setAddressId(userForm.getAddressId());
        return pojo;
    }
    public HotelData convertHotelPojoToData(HotelPojo pojo) {
        HotelData data = new HotelData();
        data.setId(pojo.getId());
        data.setName(pojo.getName());
        data.setDescription(pojo.getDescription());
        data.setRating(pojo.getRating());
        data.setAmenities(pojo.getAmenities());
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
}
