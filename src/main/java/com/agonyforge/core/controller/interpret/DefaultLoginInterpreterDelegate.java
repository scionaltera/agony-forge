package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.config.LoginConfiguration;
import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.DefaultLoginConnectionState;
import com.agonyforge.core.repository.ConnectionRepository;
import com.agonyforge.core.repository.CreatureRepository;
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
import java.util.Optional;

import static com.agonyforge.core.model.DefaultLoginConnectionState.*;
import static com.agonyforge.core.model.PrimaryConnectionState.DISCONNECTED;
import static com.agonyforge.core.model.PrimaryConnectionState.IN_GAME;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

public class DefaultLoginInterpreterDelegate implements LoginInterpreterDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLoginInterpreterDelegate.class);

    private LoginConfiguration loginConfiguration;
    private UserDetailsManager userDetailsManager;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private SessionRepository sessionRepository;
    private ConnectionRepository connectionRepository;
    private CreatureRepository creatureRepository;
    private CommService commService;

    public DefaultLoginInterpreterDelegate(
        LoginConfiguration loginConfiguration,
        UserDetailsManager userDetailsManager,
        AuthenticationManager authenticationManager,
        SessionRepository sessionRepository,
        ConnectionRepository connectionRepository,
        CreatureRepository creatureRepository,
        CommService commService) {

        this.loginConfiguration = loginConfiguration;
        this.userDetailsManager = userDetailsManager;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.sessionRepository = sessionRepository;
        this.connectionRepository = connectionRepository;
        this.creatureRepository = creatureRepository;
        this.commService = commService;
    }

    @Transactional
    @Override
    public Output interpret(Interpreter primary, Input input, Connection connection) {
        Output output = new Output();
        DefaultLoginConnectionState secondaryState = DefaultLoginConnectionState.valueOf(connection.getSecondaryState());

        switch (secondaryState) {
            case RECONNECT:
                if (!StringUtils.isEmpty(input.toString())) {
                    connection.setSecondaryState(DEFAULT.name());
                } else {
                    findOrBuildCreature(primary, connection.getName(), connection);

                    output.append("[yellow]Welcome back, " + connection.getName() + "!");
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
                    connection.setSecondaryState(LOGIN_ASK_PASSWORD.name());
                } catch (InvalidInputException e) {
                    output.append("[red]" + e.getMessage());
                }
                break;
            case LOGIN_ASK_PASSWORD:
                try {
                    logUserIn(connection.getName(), input.toString(), connection);
                    findOrBuildCreature(primary, connection.getName(), connection);

                    output.append("[yellow]Welcome back, " + connection.getName() + "!");

                    LOGGER.info("Successful login {} {}@{}", connection.getName(), connection.getSessionId(), connection.getRemoteAddress());
                } catch (BadCredentialsException e) {
                    output.append("[red]Sorry! Please try again!");
                    LOGGER.warn("Bad password attempt for {} {}@{}", connection.getName(), connection.getSessionId(), connection.getRemoteAddress());
                    connection.setSecondaryState(DEFAULT.name());
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
                    connection.setSecondaryState(CREATE_CHOOSE_PASSWORD.name());
                } else {
                    connection.setSecondaryState(CREATE_CHOOSE_NAME.name());
                }
                break;
            case CREATE_CHOOSE_PASSWORD:
                try {
                    User user = new User(
                        connection.getName(),
                        passwordEncoder.encode(validatePassword(input.toString())),
                        true,
                        true,
                        true,
                        true,
                        Collections.singletonList(new SimpleGrantedAuthority("PLAYER")));

                    userDetailsManager.createUser(user);

                    logUserIn(connection.getName(), validatePassword(input.toString()), connection);
                    connection.setSecondaryState(CREATE_CONFIRM_PASSWORD.name());
                } catch (InvalidInputException e) {
                    output.append("[red]" + e.getMessage());
                } catch (BadCredentialsException e) {
                    output.append("[red]Oops! Something bad happened. The error has been logged.");
                    LOGGER.error("Unable to log in newly created player!", e);
                }

                break;
            case CREATE_CONFIRM_PASSWORD:
                try {
                    logUserIn(connection.getName(), validatePassword(input.toString()), connection);
                    findOrBuildCreature(primary, connection.getName(), connection);

                    output.append("[yellow]Welcome, " + connection.getName() + "!");

                    LOGGER.info("New player {} {}@{}", connection.getName(), connection.getSessionId(), connection.getRemoteAddress());
                } catch (InvalidInputException e) {
                    output.append("[red]" + e.getMessage());
                } catch (BadCredentialsException e) {
                    output.append("[red]Passwords do not match. Please try again!");
                    userDetailsManager.deleteUser(connection.getName());
                    connection.setSecondaryState(CREATE_CHOOSE_PASSWORD.name());
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

            case LOGIN_ASK_PASSWORD:
            case CREATE_CHOOSE_PASSWORD:
            case CREATE_CONFIRM_PASSWORD:
                return new Output(loginConfiguration.getPrompt(secondaryState.toProperty(), connection)).setSecret(true);

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

    private String validatePassword(String in) throws InvalidInputException {
        if (in.length() < 8) {
            throw new InvalidInputException("Passwords must be at least 8 characters.");
        }

        return in;
    }

    @SuppressWarnings("unchecked")
    private void logUserIn(String name, String password, Connection connection) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(name, password);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Session session = sessionRepository.findById(connection.getHttpSessionId());

        securityContext.setAuthentication(authentication);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);

        sessionRepository.save(session);
    }

    private void findOrBuildCreature(Interpreter primary, String name, Connection connection) {
        Optional<Creature> optionalCreature = creatureRepository
            .findByConnectionIsNotNull()
            .filter(c -> !c.getConnection().equals(connection))
            .filter(c -> c.getName().equals(name))
            .findFirst();

        Creature creature = optionalCreature.orElseGet(() -> {
            Creature c = new Creature();

            c.setName(name);

            return c;
        });

        if (creature.getConnection() != null) {
            Connection oldConnection = creature.getConnection();

            oldConnection.setPrimaryState(DISCONNECTED);

            connectionRepository.save(oldConnection);

            commService.echo(creature, primary, new Output("[yellow]This character has been reconnected in another browser. Goodbye!"));
        }

        creature.setConnection(connection);

        creatureRepository.save(creature);

        connection.setPrimaryState(IN_GAME);
        connection.setSecondaryState(null);
    }
}
