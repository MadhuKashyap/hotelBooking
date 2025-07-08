package com.example.hotelBooking.form;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelFilterForm {
    private String startDate;
    private String endDate;
    private Integer ratings;
    private Double priceStart;
    private Double priceEnd;
} 