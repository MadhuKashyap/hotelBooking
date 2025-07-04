package com.example.hotelBooking.rest;

import com.example.hotelBooking.form.HotelFilterForm;
import com.example.hotelBooking.dto.HotelDto;
import com.example.hotelBooking.model.data.HotelData;
import com.example.hotelBooking.pojo.HotelPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelDto hotelDto;

    @PostMapping("/fetch")
    public List<HotelData> fetchHotels(@RequestBody HotelFilterForm filterForm) {
        return hotelDto.fetchHotels(filterForm);
    }

}
