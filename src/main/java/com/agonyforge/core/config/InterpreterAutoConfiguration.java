package com.agonyforge.core.config;

import com.agonyforge.core.controller.interpret.delegate.creation.CharacterCreationInterpreterDelegate;
import com.agonyforge.core.controller.interpret.delegate.creation.DefaultCharacterCreationInterpreterDelegate;
import com.agonyforge.core.controller.interpret.delegate.game.DefaultInGameInterpreterDelegate;
import com.agonyforge.core.controller.interpret.delegate.login.DefaultLoginInterpreterDelegate;
import com.agonyforge.core.controller.interpret.delegate.game.InGameInterpreterDelegate;
import com.agonyforge.core.controller.interpret.delegate.login.LoginInterpreterDelegate;
import com.agonyforge.core.model.factory.CreatureFactory;
import com.agonyforge.core.model.repository.ConnectionRepository;
import com.agonyforge.core.model.repository.CreatureDefinitionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.model.repository.ZoneRepository;
import com.agonyforge.core.service.CommService;
import com.agonyforge.core.service.InvokerService;
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
    private CreatureDefinitionRepository creatureDefinitionRepository;
    private ZoneRepository zoneRepository;
    private CreatureFactory creatureFactory;
    private InvokerService invokerService;
    private CommService commService;

    @Inject
    public InterpreterAutoConfiguration(
        LoginConfiguration loginConfiguration,
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") UserDetailsManager userDetailsManager,
        AuthenticationManager authenticationManager,
        SessionRepository sessionRepository,
        ConnectionRepository connectionRepository,
        CreatureRepository creatureRepository,
        CreatureDefinitionRepository creatureDefinitionRepository,
        ZoneRepository zoneRepository,
        CreatureFactory creatureFactory,
        InvokerService invokerService,
        CommService commService) {

        this.loginConfiguration = loginConfiguration;
        this.userDetailsManager = userDetailsManager;
        this.authenticationManager = authenticationManager;
        this.sessionRepository = sessionRepository;
        this.connectionRepository = connectionRepository;
        this.creatureRepository = creatureRepository;
        this.creatureDefinitionRepository = creatureDefinitionRepository;
        this.zoneRepository = zoneRepository;
        this.creatureFactory = creatureFactory;
        this.invokerService = invokerService;
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
            creatureDefinitionRepository,
            creatureFactory,
            commService
        );
    }

    @Bean
    @ConditionalOnMissingBean(CharacterCreationInterpreterDelegate.class)
    public CharacterCreationInterpreterDelegate characterCreationInterpreterDelegate() {
        return new DefaultCharacterCreationInterpreterDelegate(
            creatureFactory,
            creatureRepository,
            creatureDefinitionRepository,
            zoneRepository,
            commService
        );
    }

    @Bean
    @ConditionalOnMissingBean(InGameInterpreterDelegate.class)
    public InGameInterpreterDelegate inGameInterpreterDelegate() {
        return new DefaultInGameInterpreterDelegate(
            creatureRepository,
            loginConfiguration,
            invokerService);
    }
}
