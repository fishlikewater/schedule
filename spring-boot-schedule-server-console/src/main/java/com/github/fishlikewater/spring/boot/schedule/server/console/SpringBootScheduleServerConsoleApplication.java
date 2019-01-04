package com.github.fishlikewater.spring.boot.schedule.server.console;

import com.fishlikewater.spring.boot.schedule.server.annotion.EnableClusterScheduleServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableClusterScheduleServer
public class SpringBootScheduleServerConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootScheduleServerConsoleApplication.class, args);
    }

}

