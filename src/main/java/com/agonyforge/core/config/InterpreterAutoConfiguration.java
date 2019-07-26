package com.agonyforge.core.config;

import com.agonyforge.core.controller.interpret.CharacterCreationInterpreterDelegate;
import com.agonyforge.core.controller.interpret.DefaultCharacterCreationInterpreterDelegate;
import com.agonyforge.core.controller.interpret.DefaultInGameInterpreterDelegate;
import com.agonyforge.core.controller.interpret.DefaultLoginInterpreterDelegate;
import com.agonyforge.core.controller.interpret.InGameInterpreterDelegate;
import com.agonyforge.core.controller.interpret.LoginInterpreterDelegate;
import com.agonyforge.core.model.CreatureFactory;
import com.agonyforge.core.repository.ConnectionRepository;
import com.agonyforge.core.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
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
    private CreatureFactory creatureFactory;
    private CommService commService;

    @Inject
    public InterpreterAutoConfiguration(
        LoginConfiguration loginConfiguration,
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") UserDetailsManager userDetailsManager,
        AuthenticationManager authenticationManager,
        SessionRepository sessionRepository,
        ConnectionRepository connectionRepository,
        CreatureRepository creatureRepository,
        CreatureFactory creatureFactory,
        CommService commService) {

        this.loginConfiguration = loginConfiguration;
        this.userDetailsManager = userDetailsManager;
        this.authenticationManager = authenticationManager;
        this.sessionRepository = sessionRepository;
        this.connectionRepository = connectionRepository;
        this.creatureRepository = creatureRepository;
        this.creatureFactory = creatureFactory;
        this.commService = commService;
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
            creatureRepository,
            creatureFactory,
            commService
        );
    }

    @Bean
    @ConditionalOnMissingBean(CharacterCreationInterpreterDelegate.class)
    public CharacterCreationInterpreterDelegate characterCreationInterpreterDelegate() {
        return new DefaultCharacterCreationInterpreterDelegate(
            creatureFactory,
            creatureRepository
        );
    }

    @Bean
    @ConditionalOnMissingBean(InGameInterpreterDelegate.class)
    public InGameInterpreterDelegate inGameInterpreterDelegate() {
        return new DefaultInGameInterpreterDelegate(
            creatureRepository,
            loginConfiguration,
            commService);
    }
}
