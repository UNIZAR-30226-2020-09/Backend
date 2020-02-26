package com.Backend.Application;

import com.Backend.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		//Lo suyo es trabajar con un interfaz para no tener que cambiar
		//codigo en caso de herencia.
		User usuario = context.getBean("usuario", User.class);
		System.out.println("Probando beans");
	}

}
