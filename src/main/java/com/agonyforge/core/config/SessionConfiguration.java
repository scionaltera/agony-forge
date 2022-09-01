package com.agonyforge.core.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

@EnableHazelcastHttpSession
@Configuration
public class SessionConfiguration {
    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();

        config.setClusterName("agony-forge-sessions");

        return Hazelcast.newHazelcastInstance(config);
    }
}
