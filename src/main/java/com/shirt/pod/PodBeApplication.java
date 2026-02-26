package com.shirt.pod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PodBeApplication {
    public static void main(String[] args) {
        SpringApplication.run(PodBeApplication.class, args);
    }
}
