package com.example.hotelBooking.model.form;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String role;
}