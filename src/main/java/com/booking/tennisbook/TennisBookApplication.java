package com.booking.tennisbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TennisBookApplication {
    public static void main(String[] args) {
        SpringApplication.run(TennisBookApplication.class, args);
    }
} 