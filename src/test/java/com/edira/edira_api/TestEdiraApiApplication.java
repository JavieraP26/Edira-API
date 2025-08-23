package com.edira.edira_api;

import org.springframework.boot.SpringApplication;

public class TestEdiraApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(EdiraApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
