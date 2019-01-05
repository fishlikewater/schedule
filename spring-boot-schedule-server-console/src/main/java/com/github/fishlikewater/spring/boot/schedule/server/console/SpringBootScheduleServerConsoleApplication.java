package com.github.fishlikewater.spring.boot.schedule.server.console;

import com.fishlikewater.spring.boot.schedule.server.annotion.EnableScheduleServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableClusterScheduleServer
@EnableScheduleServer
public class SpringBootScheduleServerConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootScheduleServerConsoleApplication.class, args);
    }

}

