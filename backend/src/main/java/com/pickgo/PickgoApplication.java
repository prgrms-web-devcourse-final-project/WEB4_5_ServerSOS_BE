package com.pickgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PickgoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PickgoApplication.class, args);
    }

}
