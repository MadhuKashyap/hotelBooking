package com.example.hotelBooking.dto;

import com.example.hotelBooking.dao.UserDao;
import com.example.hotelBooking.pojo.UserPojo;
import com.example.hotelBooking.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

} 