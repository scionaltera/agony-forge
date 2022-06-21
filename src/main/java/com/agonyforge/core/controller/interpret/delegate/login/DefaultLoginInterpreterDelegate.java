package com.agonyforge.core.controller.interpret.delegate.login;

import com.agonyforge.core.config.LoginConfiguration;
import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureDefinition;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.factory.CreatureFactory;
import com.agonyforge.core.model.Gender;
import com.agonyforge.core.model.factory.ZoneFactory;
import com.agonyforge.core.model.repository.ConnectionRepository;
import com.agonyforge.core.model.repository.CreatureDefinitionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Collections;

import static com.agonyforge.core.controller.interpret.delegate.login.DefaultLoginConnectionState.*;
import static com.agonyforge.core.controller.interpret.PrimaryConnectionState.*;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

public class DefaultLoginInterpreterDelegate implements LoginInterpreterDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLoginInterpreterDelegate.class);

    private final LoginConfiguration loginConfiguration;
    private final UserDetailsManager userDetailsManager;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final SessionRepository<Session> sessionRepository;
    private final ConnectionRepository connectionRepository;
    private final CreatureRepository creatureRepository;
    private final CreatureDefinitionRepository creatureDefinitionRepository;
    private final ZoneFactory zoneFactory;
    private final CreatureFactory creatureFactory;
    private final CommService commService;

    public DefaultLoginInterpreterDelegate(
        LoginConfiguration loginConfiguration,
        UserDetailsManager userDetailsManager,
        AuthenticationManager authenticationManager,
        SessionRepository<Session> sessionRepository,
        ConnectionRepository connectionRepository,
        CreatureRepository creatureRepository,
        CreatureDefinitionRepository creatureDefinitionRepository,
        ZoneFactory zoneFactory,
        CreatureFactory creatureFactory,
        CommService commService) {

        this.loginConfiguration = loginConfiguration;
        this.userDetailsManager = userDetailsManager;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.sessionRepository = sessionRepository;
        this.connectionRepository = connectionRepository;
        this.creatureRepository = creatureRepository;
        this.creatureDefinitionRepository = creatureDefinitionRepository;
        this.zoneFactory = zoneFactory;
        this.creatureFactory = creatureFactory;
        this.commService = commService;
    }

    @Transactional
    @Override
    public Output interpret(Interpreter primary, Input input, Connection connection) {
        return interpret(primary, input, connection, true);
    }

    @Transactional
    @Override
    public Output interpret(Interpreter primary, Input input, Connection connection, boolean showPrompt) {
        Creature creature;
        Output output = new Output();
        DefaultLoginConnectionState secondaryState = DefaultLoginConnectionState.valueOf(connection.getSecondaryState());

        switch (secondaryState) {
            case RECONNECT:
                if (!StringUtils.hasText(input.toString())) {
                    connection.setSecondaryState(DEFAULT.name());
                } else {
                    creature = findOrBuildPlayer(connection.getName(), primary, connection);
                    placePlayerInWorld(creature);

                    output.append("[yellow]Welcome back, " + connection.getName() + "!");
                    output.append(primary.interpret(new Input("look"), creature.getConnection(), false));

                    commService.echoToWorld(new Output("[yellow]" + creature.getName() + " has reconnected."), primary, creature);

                    LOGGER.info("Reconnected {} {}@{}", connection.getName(), connection.getSessionId(), connection.getRemoteAddress());
                }
                break;
            case DEFAULT:
                if (input.toString().equalsIgnoreCase("Y")) {
                    connection.setSecondaryState(CREATE_CHOOSE_NAME.name());
                } else {
                    connection.setSecondaryState(LOGIN_ASK_NAME.name());
                }
                break;
            case LOGIN_ASK_NAME:
                try {
                    connection.setName(validateName(input.toString()));

                    logUserIn(connection.getName(), connection);
                    creature = findOrBuildPlayer(connection.getName(), primary, connection);
                    placePlayerInWorld(creature);

                    output.append("[yellow]Welcome back, " + connection.getName() + "!");
                    output.append(primary.interpret(new Input("look"), creature.getConnection(), false));

                    commService.echoToWorld(new Output("[yellow]" + creature.getName() + " has entered the game."), primary, creature);

                    LOGGER.info("Successful login {} {}@{}", connection.getName(), connection.getSessionId(), connection.getRemoteAddress());
                } catch (BadCredentialsException e) {
                    output.append("[red]Sorry! Please try again!");
                    LOGGER.warn("Bad password attempt for {} {}@{}", connection.getName(), connection.getSessionId(), connection.getRemoteAddress());
                    connection.setSecondaryState(DEFAULT.name());
                } catch (InvalidInputException e) {
                    output.append("[red]" + e.getMessage());
                }

                break;
            case CREATE_CHOOSE_NAME:
                try {
                    connection.setName(validateName(input.toString()));

                    if (userDetailsManager.userExists(connection.getName())) {
                        output.append("[red]That name is already in use. Please try another!");
                        connection.setName(null);
                    } else {
                        connection.setSecondaryState(CREATE_CONFIRM_NAME.name());
                    }
                } catch (InvalidInputException e) {
                    output.append("[red]" + e.getMessage());
                }
                break;
            case CREATE_CONFIRM_NAME:
                if (input.toString().equalsIgnoreCase("Y")) {
                    try {
                        User user = new User(
                            connection.getName(),
                            passwordEncoder.encode("password"),
                            true,
                            true,
                            true,
                            true,
                            Collections.singletonList(new SimpleGrantedAuthority("PLAYER")));

                        userDetailsManager.createUser(user);

                        logUserIn(connection.getName(), connection);
                    } catch (BadCredentialsException e) {
                        output.append("[red]Oops! Something bad happened. The error has been logged.");
                        LOGGER.error("Unable to log in newly created player!", e);
                        userDetailsManager.deleteUser(connection.getName());
                    }

                    connection.setPrimaryState(CREATION);
                    connection.setSecondaryState(null);
                } else {
                    connection.setSecondaryState(CREATE_CHOOSE_NAME.name());
                    userDetailsManager.deleteUser(connection.getName());
                }
                break;
            default:
                output.append("[red]Oops! Something went wrong. The error has been logged.");
                LOGGER.error("Reached default state in interpret()!");
        }

        return output.append(primary.prompt(connectionRepository.save(connection)));
    }

    @Transactional
    @Override
    public Output prompt(Interpreter interpreter, Connection connection) {
        DefaultLoginConnectionState secondaryState = DefaultLoginConnectionState.valueOf(connection.getSecondaryState());

        switch (secondaryState) {
            case RECONNECT:
            case DEFAULT:
            case LOGIN_ASK_NAME:
            case CREATE_CHOOSE_NAME:
            case CREATE_CONFIRM_NAME:
                return new Output(loginConfiguration.getPrompt(secondaryState.toProperty(), connection));

            default:
                LOGGER.error("Reached default state in prompt()!");
                return new Output("[red]Oops! Something went wrong. The error has been logged.");
        }
    }

    private String validateName(String in) throws InvalidInputException {
        if (in.matches(".*\\s.*")) {
            throw new InvalidInputException("Names may not contain whitespace.");
        }

        if (in.matches(".*[^a-zA-Z].*")) {
            throw new InvalidInputException("Names may only contain letters.");
        }

        if (in.length() < 3) {
            throw new InvalidInputException("Names must be at least 3 letters long.");
        }

        if (in.length() > 30) {
            throw new InvalidInputException("Names may not be longer than 30 letters.");
        }

        if (in.matches("^[^A-Z].*$")) {
            throw new InvalidInputException("Names must begin with an upper case letter.");
        }

        if (in.matches("^[A-Z].*[A-Z].*$")) {
            throw new InvalidInputException("Names must not contain upper case letters other than the first.");
        }

        return in;
    }

    private void logUserIn(String name, Connection connection) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(name, "password");
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Session session = sessionRepository.findById(connection.getHttpSessionId());

        securityContext.setAuthentication(authentication);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);

        sessionRepository.save(session);
    }

    private Creature findOrBuildPlayer(String name, Interpreter primary, Connection connection) {
        CreatureDefinition definition = creatureDefinitionRepository
            .findByPlayerIsTrueAndName(name)
            .orElseGet(() -> {
                CreatureDefinition def = new CreatureDefinition();

                def.setPlayer(true);
                def.setName(name);
                def.setGender(Gender.NEUTRAL); // this user existed prior to genders so we can't know this
                                               // they'll need to get it manually changed in the database

                return creatureDefinitionRepository.save(def);
            });

        Creature creature = creatureFactory.build(definition, primary, connection);

        connection.setPrimaryState(IN_GAME);
        connection.setSecondaryState(null);

        return creature;
    }

    private void placePlayerInWorld(Creature creature) {
        if (creature.getRoom() != null) {
            return;
        }

        Zone zone = zoneFactory.getStartZone();

        zone.getRooms()
            .stream()
            .filter(room -> room.getSequence() == 0)
            .findAny()
            .ifPresent(room -> {
                creature.setRoom(room);
                creatureRepository.save(creature);

                LOGGER.info("Placed {} in start room: {}#{}",
                    creature.getName(),
                    zone.getId(),
                    room.getSequence());
            });
    }
}
