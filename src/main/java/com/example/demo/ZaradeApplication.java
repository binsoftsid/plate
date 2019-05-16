package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com","com.auth","com.controller","com.model","com.print"})
public class ZaradeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZaradeApplication.class, args);
	}
}