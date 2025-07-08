package com.example.hotelBooking.model.form;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

public class BookingForm {
    private Long roomId;
    private Long hotelId;
    private String startDate;
    private String endDate;
}
