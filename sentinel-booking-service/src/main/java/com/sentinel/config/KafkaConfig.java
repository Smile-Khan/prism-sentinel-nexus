package com.sentinel.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String BOOKING_TOPIC = "booking-events";

    /**
     * Initializes the core booking events topic.
     * Configured with multiple partitions to allow horizontal scaling of consumers.
     */
    @Bean
    public NewTopic bookingEventsTopic() {
        return TopicBuilder.name(BOOKING_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}