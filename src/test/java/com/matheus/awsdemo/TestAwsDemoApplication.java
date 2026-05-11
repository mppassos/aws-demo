package com.matheus.awsdemo;

import org.springframework.boot.SpringApplication;

public class TestAwsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.from(AwsDemoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
