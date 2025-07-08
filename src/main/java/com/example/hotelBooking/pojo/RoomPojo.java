package com.example.hotelBooking.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.example.hotelBooking.model.enums.RoomType;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "room")
public class RoomPojo extends AbstractVersionedPojo{
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    private int roomNumber;
    private double price;
    @Column(columnDefinition = "TEXT")
    private String bookedDates;

    @Column(columnDefinition = "TEXT")
    private String amenities;    // Add fields and methods as needed
    private Long hotelId;
} 