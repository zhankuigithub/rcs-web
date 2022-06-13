package com.longmaster.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class RcsAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(RcsAdminApplication.class, args);
	}

}

