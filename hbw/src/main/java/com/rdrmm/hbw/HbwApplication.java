package com.rdrmm.hbw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HbwApplication {

    public static void main(String[] args) {
        SpringApplication.run(HbwApplication.class, args);
    }

}