package com.agonyforge.core.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "agony.datasource")
    public DataSource dataSource() {
        DataSource dataSource = DataSourceBuilder.create().build();

        // The MySQL container can take some time to start up when running under Docker Compose.
        // By default, HikariCP will fail immediately if it cannot connect. This will tell it to
        // wait up to a minute for the other container to finish starting.
        ((HikariDataSource)dataSource).setInitializationFailTimeout(60000);

        return dataSource;
    }
}
