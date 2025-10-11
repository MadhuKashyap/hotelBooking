package com.example.hotelBooking.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {
    @GetMapping("/login")
    public ModelAndView getLogin() {
        return new ModelAndView("login");
    }
    @GetMapping("/home")
    public ModelAndView getHome() {
        return new ModelAndView("home");
    }
    @GetMapping("/search")
    public ModelAndView searchBox() {
        return new ModelAndView("search");
    }
    @GetMapping("/hotel")
    public ModelAndView hotelList() { return new ModelAndView("hotel");}
}
