package com.aloc.aloc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AlocApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlocApplication.class, args);
	}

}
