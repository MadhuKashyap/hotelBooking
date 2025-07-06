package com.example.hotelBooking.pojo;

import com.example.hotelBooking.model.enums.UserType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "user",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "password"})
)
public class UserPojo {
    @Id
    private Long id;
    private String name;
    private String email;
    private String userId;
    private String password;
    private String phone;
    private String address;
    private UserType role;
    private Long addressId ;
} 