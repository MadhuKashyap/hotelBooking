package com.example.hotelBooking.pojo;


import jakarta.persistence.Version;

import java.time.ZonedDateTime;
import java.util.Date;

public class AbstractVersionedPojo {
    private Date createdAt;
    private Date updatedAt;
    @Version
    private Integer Version;
}
