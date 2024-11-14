package me.gt.snaptickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SnapTicketsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnapTicketsApplication.class, args);
    }

}
