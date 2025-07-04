package com.example.hotelBooking.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class UserPojo {
    @Id
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String role;
    private Long addressId ;
} 