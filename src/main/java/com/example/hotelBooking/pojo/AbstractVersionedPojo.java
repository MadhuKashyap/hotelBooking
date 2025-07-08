package com.example.hotelBooking.pojo;


import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Date;

@MappedSuperclass
public class AbstractVersionedPojo {
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Version
    private Integer Version;
}
