package com.example.hotelBooking.model.data;

import com.example.hotelBooking.model.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BookingHistory {
    private Long bookingId;
    private Long username;
    private Long userEmail;
    private Date startDate;
    private Date endDate;
    private Double priceTotal;
    private String hotelName;
    private String roomName;
    private String roomType;
    private String roomDescription;
    private BookingStatus status;
}
