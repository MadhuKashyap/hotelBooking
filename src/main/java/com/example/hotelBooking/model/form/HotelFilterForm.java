package com.example.hotelBooking.model.form;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelFilterForm {
    private Date startDate;
    private Date endDate;
    private Integer ratings;
    private Double priceStart;
    private Double priceEnd;
}