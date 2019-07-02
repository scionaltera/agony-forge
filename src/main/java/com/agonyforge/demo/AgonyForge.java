package com.agonyforge.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.agonyforge.core", // Pick up annotated classes from inside Agony Forge Core
    "com.agonyforge.demo"}) // TODO: Change this to your own base package
@EnableJpaRepositories({
    "com.agonyforge.core", // Pick up JPA repositories from inside Agony Forge Core
    "com.agonyforge.demo"}) // TODO: Change this to your own base package
@EntityScan({
    "com.agonyforge.core", // Pick up annotated JPA entities from inside Agony Forge Core
    "com.agonyforge.demo"}) // TODO: Change this to your own base package
public class AgonyForge {
    public static void main(String ... args) {
        SpringApplication.run(AgonyForge.class, args);
    }
}
