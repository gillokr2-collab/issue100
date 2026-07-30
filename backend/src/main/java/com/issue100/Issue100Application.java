package com.issue100;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Issue100Application {
    public static void main(String[] args) {
        SpringApplication.run(Issue100Application.class, args);
    }
}
