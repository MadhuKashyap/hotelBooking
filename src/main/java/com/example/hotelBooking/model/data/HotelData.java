package com.example.hotelBooking.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelData {
    private Long id;
    private String name;
    private String description;
    private Double rating;
    private Long addressId; // Reference to AddressPojo
}
