package com.example.hotelBooking.dto;

import com.example.hotelBooking.dao.UserDao;
import com.example.hotelBooking.pojo.UserPojo;
import com.example.hotelBooking.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDto {
    @Autowired
    private UserDao userDao;

    @Autowired
    private DtoHelper dtoHelper;
    
    public String addUser(UserForm userForm) {
        if (dtoHelper.validateUserForm(userForm)) {
            UserPojo userPojo = dtoHelper.mapToPojo(userForm);
            userDao.save(userPojo);
            return "User added successfully";
        } else {
            return "Invalid user data";
        }
    }

    public String updateUser(UserForm userForm) {
        if (dtoHelper.validateUserForm(userForm)) {
            UserPojo userPojo = dtoHelper.mapToPojo(userForm);
            userDao.save(userPojo);
            return "User updated successfully";
        } else {
            return "Invalid user data";
        }
    }

    public String signup(UserForm userForm) {
        if (!dtoHelper.validateUserForm(userForm)) {
            return "Invalid user data";
        }
        
        // Check if user already exists
        UserPojo existingUser = userDao.findByUserIdAndPassword(userForm.getUserId(), userForm.getPassword());
        if (existingUser != null) {
            return "User already exists with this userId";
        }
        
        // Create new user
        UserPojo userPojo = dtoHelper.mapToPojo(userForm);
        userDao.save(userPojo);
        return "User registered successfully";
    }

    public String login(UserForm userForm) {
        if (userForm.getUserId() == null || userForm.getUserId().trim().isEmpty() ||
            userForm.getPassword() == null || userForm.getPassword().trim().isEmpty()) {
            return "Invalid login credentials";
        }
        
        // Find user by userId and password
        UserPojo user = userDao.findByUserIdAndPassword(userForm.getUserId(), userForm.getPassword());
        if (user != null) {
            return "Login successful";
        } else {
            return "Invalid userId or password";
        }
    }
} 