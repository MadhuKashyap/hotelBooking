package com.example.hotelBooking.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hotel")
public class HotelPojo {
    @Id
    private Long id;
    private String name;
    private String description;
    private Double rating;
    private String amenities; // Comma-separated or use a List with @ElementCollection if needed
    private Long addressId; // Reference to AddressPojo
} 