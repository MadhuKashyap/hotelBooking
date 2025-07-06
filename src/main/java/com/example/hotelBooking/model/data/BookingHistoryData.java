package com.example.hotelBooking.model.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookingHistoryData {
    List<BookingHistory> userHistory;
}
