package com.example.hotelBooking.pojo;

import com.example.hotelBooking.model.enums.AddressType;
import jakarta.persistence.*;

@Entity
@Table(name = "address")
public class AddressPojo {
    @Id
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private double latitude;
    private double longitude;
    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    // Add fields and methods as needed
} 