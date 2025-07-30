package com.capstone2025.team7.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BackendApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApiApplication.class, args);
	}

}
