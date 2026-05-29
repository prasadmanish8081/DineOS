package com.dineos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DineOsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DineOsApplication.class, args);
    }
}
