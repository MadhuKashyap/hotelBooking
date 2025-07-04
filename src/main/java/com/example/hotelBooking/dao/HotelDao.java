package com.example.hotelBooking.dao;

import com.example.hotelBooking.pojo.HotelPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelDao extends JpaRepository<HotelPojo, Long> {
} 