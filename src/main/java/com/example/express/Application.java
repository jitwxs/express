package com.example.express;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.error(
                "\n\n-------------------------------- " +
                        "Express application started successfully" +
                        " --------------------------------\n\n");
    }

}
