package com.lowquality.serverwebm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.lowquality.serverwebm.repository")
@EntityScan(basePackages = "com.lowquality.serverwebm.models.entity")
public class ServerWebMApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerWebMApplication.class, args);
    }

}
