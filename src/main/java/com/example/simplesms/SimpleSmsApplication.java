package com.example.simplesms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SimpleSmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSmsApplication.class, args);
    }

}
