package com.example.hotelBooking.config;

import com.example.hotelBooking.dao.UserDao;
import com.example.hotelBooking.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // Since our UserDao doesn't have findByUserId method, we'll need to implement a workaround
        // For now, we'll return null and handle authentication in a custom way
        throw new UsernameNotFoundException("User not found with userId: " + userId);
    }

    public UserDetails loadUserByUserIdAndPassword(String userId, String password) {
        UserPojo userPojo = userDao.findByUserIdAndPassword(userId, password);
        if (userPojo != null) {
            return User.builder()
                    .username(userPojo.getUserId())
                    .password(userPojo.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userPojo.getRole().name())))
                    .build();
        }
        return null;
    }
} 