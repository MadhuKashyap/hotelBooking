package com.example.hotelBooking.dto;

import com.example.hotelBooking.dao.RoomDao;
import com.example.hotelBooking.model.data.HotelData;
import com.example.hotelBooking.pojo.HotelPojo;
import com.example.hotelBooking.form.HotelFilterForm;
import com.example.hotelBooking.pojo.RoomPojo;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DtoHelper {
    @Autowired
    private RoomDao roomDao;
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

    public static HotelData convertHotelPojoToData(HotelPojo pojo) {
        HotelData data = new HotelData();
        data.setId(pojo.getId());
        data.setName(pojo.getName());
        data.setDescription(pojo.getDescription());
        data.setRating(pojo.getRating());
        data.setAmenities(pojo.getAmenities());
        data.setAddressId(pojo.getAddressId());
        return data;
    }
}
