package com.example.hotelBooking.rest;

import com.example.hotelBooking.dto.UserDto;
import com.example.hotelBooking.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDto userDto;

    @PostMapping("/add")
    public String addUser(@RequestBody UserForm userForm) {
        return userDto.addUser(userForm);
    }

    @PutMapping("/update")
    public String updateUser(@RequestBody UserForm userForm) {
        return userDto.updateUser(userForm);
    }
} 