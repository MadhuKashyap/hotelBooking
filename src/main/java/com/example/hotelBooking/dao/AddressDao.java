package com.example.hotelBooking.dao;

import com.example.hotelBooking.pojo.AddressPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressDao extends JpaRepository<AddressPojo, Long> {
} 