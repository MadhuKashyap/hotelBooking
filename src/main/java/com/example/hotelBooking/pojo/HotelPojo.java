package com.example.hotelBooking.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hotel", uniqueConstraints = @UniqueConstraint(columnNames = {"id", "col"}))
public class HotelPojo {
    @Id
    private Long id;
    private String name;
    private String description;
    private Double rating;
    private Long addressId; // Reference to AddressPojo
} 