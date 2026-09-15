package com.procurepilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProcurePilotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcurePilotApplication.class, args);
    }
}
