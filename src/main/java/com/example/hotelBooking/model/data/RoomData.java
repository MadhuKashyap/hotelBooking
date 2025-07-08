package com.example.hotelBooking.model.data;


import com.example.hotelBooking.model.enums.RoomType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RoomData {
    private Long id;
    private RoomType roomType;
    private int roomNumber;
    private double price;
    private List<String> bookedDates;
    private List<String> amenities;
}
