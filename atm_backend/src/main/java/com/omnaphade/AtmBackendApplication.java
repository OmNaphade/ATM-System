package com.omnaphade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AtmBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtmBackendApplication.class, args);
    }

}
