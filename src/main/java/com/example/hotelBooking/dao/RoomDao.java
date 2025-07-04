package com.example.hotelBooking.dao;

import com.example.hotelBooking.pojo.RoomPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomDao extends JpaRepository<RoomPojo, Long> {
    List<RoomPojo> findByHotelId(Long hotelId);
} 
 