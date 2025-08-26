package com.edira.edira_api;

import org.springframework.boot.SpringApplication;

/*
 * Clase principal de la aplicación de pruebas.
 * Se usa para iniciar la aplicación con Testcontainers.
 */

public class TestEdiraApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(EdiraApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
