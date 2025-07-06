package com.example.hotelBooking.rest;

import com.example.hotelBooking.form.HotelFilterForm;
import com.example.hotelBooking.dto.HotelDto;
import com.example.hotelBooking.form.UserForm;
import com.example.hotelBooking.model.data.BookingHistoryData;
import com.example.hotelBooking.model.data.HotelData;
import com.example.hotelBooking.model.data.RoomData;
import com.example.hotelBooking.model.form.BookingForm;
import com.example.hotelBooking.pojo.HotelPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelDto hotelDto;

    @PostMapping("/fetch-all")
    public List<HotelData> fetchHotels(@RequestBody HotelFilterForm filterForm) {
        return hotelDto.fetchHotels(filterForm);
    }
    @GetMapping("/fetch-rooms")
    public List<RoomData> fetchAvailableHotelRooms(@RequestParam Long hotelId) {
        return hotelDto.fetchRoomsByHotelId(hotelId);
    }
    @PostMapping("/book-room")
    public String bookRoom(@RequestBody BookingForm bookingForm) throws Exception {
        return hotelDto.bookRoom(bookingForm);
    }
    @GetMapping("/cancel-room")
    public String cancelRoom(@RequestParam Long bookingId) throws Exception {
        return hotelDto.cancelRoom(bookingId);
    }
    @PostMapping("/view-bookings")
    public BookingHistoryData viewBookingHistory(@RequestBody UserForm userForm) {
        return hotelDto.viewBookingHistory(userForm);
    }


}
