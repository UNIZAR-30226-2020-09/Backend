package com.Backend;

import com.Backend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
//@RestController
public class BackendApplication {

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World!";
	}

	public static void main(String[] args) {
		com.Backend.model.User nuevo = new User("alex", "alexcorreo");
		SpringApplication.run(BackendApplication.class, args);
	}

	private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);
}