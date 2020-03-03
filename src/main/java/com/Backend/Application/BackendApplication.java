package com.Backend.Application;

import com.Backend.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.Backend.model.*;

@SpringBootApplication
//@RestController
public class BackendApplication {

	public static void main(String[] args) {
		//ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		//Lo suyo es trabajar con un interfaz para no tener que cambiar
		//codigo en caso de herencia.
		//User usuario = context.getBean("usuarioPepe", User.class);
		//System.out.println("Probando beans");
		SpringApplication.run(BackendApplication.class, args);
	}

	private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

	/*@Bean
	public CommandLineRunner demo(UserRepository repository) {
		return (args) -> {
			// save a few customers
			repository.save(new User("Alejandro", "alex@gg.com"));
			repository.save(new User("Alejandro", "gogoalex@df.com"));
			repository.save(new User("Raul H.", "rrrrr@gmail.com"));
			repository.save(new User("Sergio", "Alejandro"));
			repository.save(new User("Alejandro", "Magno"));

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (User usuario : repository.findAll()) {
				log.info(usuario.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			User usuario = repository.findById(1L);
			log.info("User found with findById(1L):");
			log.info("--------------------------------");
			log.info(usuario.toString());
			log.info("");

			// fetch customers by last name
			log.info("Customer found with findByLastName('Bauer'):");
			log.info("--------------------------------------------");
			repository.findByUserName("Alejandro").forEach(elegido -> {
				log.info(elegido.toString());
			});
			// for (Customer bauer : repository.findByLastName("Bauer")) {
			//  log.info(bauer.toString());
			// }
			log.info("");
		};
	}*/

	/*@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}*/

}
