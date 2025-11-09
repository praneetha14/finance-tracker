package com.finance.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.finance.tracker.entity")
public class FinanceTrackerApplication {

    public static void main(String[] args) {

        SpringApplication.run(FinanceTrackerApplication.class, args);
    }

}
