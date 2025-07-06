package com.example.hotelBooking.dao;

import com.example.hotelBooking.pojo.UserPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<UserPojo, Long> {
    UserPojo findByUserIdPassword(String userId, String password);
} 