package com.ufg.runner.assinador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class AssinadorApplication {

    private static final java.util.Set<String> CLI_COMMANDS = java.util.Set.of("sign", "validate");

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(AssinadorApplication.class);

        boolean isCliCommand = args.length > 0 && CLI_COMMANDS.contains(args[0]);

        app.setWebApplicationType(isCliCommand ? WebApplicationType.NONE : WebApplicationType.SERVLET);

        app.run(args);
    }
}
