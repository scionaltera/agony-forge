package com.agonyengine.forge.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.agonyengine.forge", // Pick up annotated classes from inside Agony Forge Core
    "com.agonyengine.forge.demo"}) // Pick up annotated classes from this project
@EnableJpaRepositories({
    "com.agonyengine.forge", // Pick up JPA repositories from inside Agony Forge Core
    "com.agonyengine.forge.demo"}) // Pick up JPA repositories from this project
@EntityScan({
    "com.agonyengine.forge",
    "com.agonyengine.forge.demo"})
public class AgonyForge {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgonyForge.class);

    public static void main(String ... args) {
        System.getenv().keySet().forEach(key -> LOGGER.info("ENV: {} -> {}", key, System.getenv(key)));

        SpringApplication.run(AgonyForge.class, args);
    }
}
