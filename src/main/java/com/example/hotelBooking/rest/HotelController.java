package com.example.hotelBooking.rest;

import com.example.hotelBooking.form.HotelFilterForm;
import com.example.hotelBooking.dto.HotelDto;
import com.example.hotelBooking.form.UserForm;
import com.example.hotelBooking.model.data.BookingHistoryData;
import com.example.hotelBooking.model.data.HotelData;
import com.example.hotelBooking.model.data.RoomData;
import com.example.hotelBooking.model.form.BookingForm;
import com.example.hotelBooking.pojo.HotelPojo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelDto hotelDto;

    @PostMapping("/fetch-all")
    public Page<HotelData> fetchHotels(@RequestBody HotelFilterForm filterForm,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) throws JsonProcessingException {
        return hotelDto.fetchHotels(filterForm, page, size);
    }
    @GetMapping("/fetch-rooms")
    public Page<RoomData> fetchAvailableHotelRooms(@RequestParam Long hotelId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) throws JsonProcessingException {
        return hotelDto.fetchRoomsByHotelId(hotelId, page, size);
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
