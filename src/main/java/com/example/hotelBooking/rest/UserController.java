package com.example.hotelBooking.rest;

import com.example.hotelBooking.dto.UserDto;
import com.example.hotelBooking.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody UserForm userForm) {
        Map<String, Object> response = new HashMap<>();
        String result = userDto.signup(userForm);
        response.put("message", result);
        response.put("success", result.contains("successfully"));
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserForm userForm) {
        Map<String, Object> response = new HashMap<>();
        String result = userDto.login(userForm);
        response.put("message", result);
        response.put("success", result.contains("successfully"));
        return response;
    }

    @PostMapping("/users/login")
    public Map<String, Object> loginWithPath(@RequestBody UserForm userForm) {
        return login(userForm);
    }
} 