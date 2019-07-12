package com.agonyforge.demo.config;

import com.agonyforge.demo.controller.interpret.DefaultInGameInterpreterDelegate;
import com.agonyforge.demo.controller.interpret.DefaultLoginInterpreterDelegate;
import com.agonyforge.demo.controller.interpret.InGameInterpreterDelegate;
import com.agonyforge.demo.controller.interpret.LoginInterpreterDelegate;
import com.agonyforge.demo.repository.ConnectionRepository;
import com.agonyforge.demo.repository.CreatureRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.session.SessionRepository;

import javax.inject.Inject;

@Configuration
public class InterpreterAutoConfiguration {
    private LoginConfiguration loginConfiguration;
    private UserDetailsManager userDetailsManager;
    private AuthenticationManager authenticationManager;
    private SessionRepository sessionRepository;
    private ConnectionRepository connectionRepository;
    private CreatureRepository creatureRepository;

    @Inject
    public InterpreterAutoConfiguration(
        LoginConfiguration loginConfiguration,
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") UserDetailsManager userDetailsManager,
        AuthenticationManager authenticationManager,
        SessionRepository sessionRepository,
        ConnectionRepository connectionRepository,
        CreatureRepository creatureRepository) {

        this.loginConfiguration = loginConfiguration;
        this.userDetailsManager = userDetailsManager;
        this.authenticationManager = authenticationManager;
        this.sessionRepository = sessionRepository;
        this.connectionRepository = connectionRepository;
        this.creatureRepository = creatureRepository;
    }

    @Bean
    @ConditionalOnMissingBean(LoginInterpreterDelegate.class)
    public LoginInterpreterDelegate loginInterpreterDelegate() {
        return new DefaultLoginInterpreterDelegate(
            loginConfiguration,
            userDetailsManager,
            authenticationManager,
            sessionRepository,
            connectionRepository,
            creatureRepository
        );
    }

    @Bean
    @ConditionalOnMissingBean(InGameInterpreterDelegate.class)
    public InGameInterpreterDelegate inGameInterpreterDelegate() {
        return new DefaultInGameInterpreterDelegate(creatureRepository, loginConfiguration);
    }
}
