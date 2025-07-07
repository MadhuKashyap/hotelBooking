package com.example.hotelBooking.dao;

import com.example.hotelBooking.pojo.BookingHistoryPojo;
import com.example.hotelBooking.pojo.UserPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingHistoryDao extends JpaRepository<BookingHistoryPojo, Long> {
    List<BookingHistoryPojo> findByUserId(Long userId);
}
