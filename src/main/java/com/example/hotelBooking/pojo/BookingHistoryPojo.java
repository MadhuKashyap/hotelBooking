package com.example.hotelBooking.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "booking_history")
public class BookingHistoryPojo {
    @Id
    private Long id;
    // Add fields and methods as needed
} 