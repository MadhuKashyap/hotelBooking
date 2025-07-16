package com.example.hotelBooking.service;

import com.example.hotelBooking.model.data.BookingHistory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBookingEvent(String topic, String event) {
        kafkaTemplate.send(topic, event);
    }
}