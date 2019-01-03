package com.fishlikewater.spring.boot.schedule.server;

import com.fishlikewater.spring.boot.schedule.server.annotion.EnableClusterScheduleServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableClusterScheduleServer
public class SpringBootScheduleServerSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootScheduleServerSampleApplication.class, args);
    }

}

