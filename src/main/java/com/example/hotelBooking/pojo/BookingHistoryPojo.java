package com.example.hotelBooking.pojo;

import com.example.hotelBooking.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "booking_history", uniqueConstraints = @UniqueConstraint(columnNames = {"userId, roomId"}))
public class BookingHistoryPojo {
    @Id
    private Long id;
    private Long userId;
    private Long roomId;
    private Double priceTotal;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    private Date startDate;
    private Date endDate;
} 