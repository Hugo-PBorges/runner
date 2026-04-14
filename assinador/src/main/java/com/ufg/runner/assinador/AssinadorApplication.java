package com.ufg.runner.assinador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AssinadorApplication {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(AssinadorApplication.class);

		if (args.length > 0) {
			app.setWebApplicationType(WebApplicationType.NONE);
		} else {
			app.setWebApplicationType(WebApplicationType.SERVLET);
		}

		app.run(args);
	}
}