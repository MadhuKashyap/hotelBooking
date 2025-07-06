package com.example.hotelBooking.model.data;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BookingHistory {
    private Long username;
    private Long userEmail;
    private Date date;
    private Double priceTotal;
    private String hotelName;
    private String roomName;
    private String roomType;
    private String roomDescription;
}
