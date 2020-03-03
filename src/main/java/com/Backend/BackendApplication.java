package com.Backend;

import com.Backend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@RestController
public class BackendApplication {

	public static void main(String[] args) {
		com.Backend.model.User nuevo = new User("alex", "alexcorreo");
		SpringApplication.run(BackendApplication.class, args);
	}

	private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);
}