package com.example.hotelBooking.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

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

    // Add fields and methods as needed
} 