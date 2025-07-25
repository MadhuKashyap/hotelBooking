package com.example.hotelBooking.dao;

import com.example.hotelBooking.pojo.UserPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<UserPojo, Long> {
    UserPojo findByUserIdAndPassword(String userId, String password);
    UserPojo findByUserId(String userId);
} 