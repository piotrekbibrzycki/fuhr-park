package com.example.fuhrpark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FuhrParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(FuhrParkApplication.class, args);
	}

}
