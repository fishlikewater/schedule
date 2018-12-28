package com.fishlikewater.client.sample;

import com.fislike.spring.boot.schedule.client.annotion.EnableClusterSchedule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableClusterSchedule
public class SpringBootScheduleClientSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootScheduleClientSampleApplication.class, args);
    }

}

