package com.example.hotelBooking.pojo;

import com.example.hotelBooking.model.enums.UserType;
import jakarta.persistence.*;
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
    @Column(unique = true)
    private String userId;
    private String password;
    private String phone;
    private String address;
    @Enumerated(EnumType.STRING)
    private UserType role;
    private Long addressId ;
} 