package com.agonyforge.core.controller.interpret.delegate.login;

import com.agonyforge.core.config.LoginConfiguration;
import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.controller.interpret.delegate.LoginConfigurationBuilder;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureDefinition;
import com.agonyforge.core.model.Room;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.factory.CreatureFactory;
import com.agonyforge.core.model.factory.ZoneFactory;
import com.agonyforge.core.model.repository.ConnectionRepository;
import com.agonyforge.core.model.repository.CreatureDefinitionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.model.repository.RoleRepository;
import com.agonyforge.core.service.CommService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static com.agonyforge.core.controller.interpret.delegate.login.DefaultLoginConnectionState.*;
import static com.agonyforge.core.controller.interpret.PrimaryConnectionState.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DefaultLoginInterpreterDelegateTest {
    @Mock
    private UserDetailsManager userDetailsManager;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SessionRepository<Session> sessionRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private CreatureRepository creatureRepository;

    @Mock
    private CreatureDefinitionRepository creatureDefinitionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ZoneFactory zoneFactory;

    @Mock
    private CommService commService;

    @Mock
    private Session session;

    @Mock
    private Interpreter primary;

    private AutoCloseable closeable;

    private DefaultLoginInterpreterDelegate interpreter;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        LoginConfiguration loginConfiguration = new LoginConfigurationBuilder().build();
        CreatureFactory creatureFactory = new CreatureFactory(
            commService,
            creatureRepository,
            connectionRepository,
            roleRepository,
            userDetailsManager);

        when(primary.interpret(any(Input.class), any(Connection.class), anyBoolean()))
            .thenAnswer(invocation -> new Output("[yellow]Look sir! Droids!"));

        when(primary.prompt(any(Connection.class))).thenAnswer(invocation -> {
            Connection connection = invocation.getArgument(0);

            if (LOGIN.equals(connection.getPrimaryState())) {
                return interpreter.prompt(primary, connection);
            } else {
                return new Output("", "[default]Dani> ");
            }
        });

        when(authenticationManager.authenticate(any(Authentication.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(sessionRepository.findById(anyString())).thenReturn(session);

        when(connectionRepository.save(any(Connection.class))).thenAnswer(invocation -> {
            Connection connection = invocation.getArgument(0);

            if (connection.getId() == null) {
                connection.setId(UUID.randomUUID());
            }

            return connection;
        });

        Creature creature = new Creature();

        when(creatureRepository.save(any())).thenAnswer(invocation -> {
            Creature c = invocation.getArgument(0);

            if (c.getId() == null) {
                c.setId(UUID.randomUUID());
            }

            return c;
        });

        when(creatureRepository.findByConnection(any(Connection.class))).thenReturn(Optional.of(creature));

        when(creatureDefinitionRepository.save(any())).thenAnswer(invocation -> {
            CreatureDefinition definition = invocation.getArgument(0);

            if (definition.getId() == null) {
                definition.setId(UUID.randomUUID());
            }

            return definition;
        });

        when(zoneFactory.getStartZone()).thenAnswer(invocation -> {
            Zone zone = new Zone();

            zone.setId(1L);

            for (int i = 0; i < 10; i++) {
                Room room = new Room();

                room.setId(UUID.randomUUID());
                room.setSequence(i);

                zone.getRooms().add(room);
            }

            return zone;
        });

        UserDetails user = mock(UserDetails.class);

        when(user.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetailsManager.loadUserByUsername(anyString())).thenReturn(user);

        interpreter = new DefaultLoginInterpreterDelegate(
            loginConfiguration,
            userDetailsManager,
            authenticationManager,
            sessionRepository,
            connectionRepository,
            creatureRepository,
            creatureDefinitionRepository,
            zoneFactory,
            creatureFactory,
            commService);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testPromptBadSecondaryState() {
        Connection connection = new Connection();
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState("INVALID");

        try {
            interpreter.prompt(primary, connection);

            fail("Required exception was not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("No enum constant"));
        }
    }

    @Test
    void testInterpretBadSecondaryState() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("input");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState("INVALID");

        try {
            interpreter.interpret(primary, input, connection);

            fail("Required exception was not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("No enum constant"));
        }
    }

    @Test
    void testPromptAskNew() {
        Connection connection = new Connection();
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(DEFAULT.name());

        Output result = interpreter.prompt(primary, connection);

        assertEquals("[default]Create a new character? [y/N]: ", result.toString());
        assertFalse(result.getSecret());
    }

    @Test
    void testInterpretAskNewNo() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("n");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(DEFAULT.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[default]Name: ", result.toString());
        assertFalse(result.getSecret());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(LOGIN_ASK_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretLoginAskShortName() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Dan");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(LOGIN_ASK_NAME.name());
        connection.setHttpSessionId(UUID.randomUUID().toString());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[yellow]Welcome back, Dan!\n[yellow]Look sir! Droids!\n\n[default]Dani> ", result.toString());
        assertEquals("Dan", connection.getName());
        assertEquals(IN_GAME, connection.getPrimaryState());
        assertEquals(DEFAULT.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretLoginAskLongName() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Danidanidanidanidanidanidanida");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(LOGIN_ASK_NAME.name());
        connection.setHttpSessionId(UUID.randomUUID().toString());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[yellow]Welcome back, Danidanidanidanidanidanidanida!\n[yellow]Look sir! Droids!\n\n[default]Dani> ", result.toString());

        assertEquals(IN_GAME, connection.getPrimaryState());
        assertEquals(DEFAULT.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretLoginAskNameWhitespace() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Dani Filth");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(LOGIN_ASK_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names may not contain whitespace.\n[default]Name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(LOGIN_ASK_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretLoginAskNameBadChars() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Dani3");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(LOGIN_ASK_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names may only contain letters.\n[default]Name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(LOGIN_ASK_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretLoginAskNameTooShort() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Da");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(LOGIN_ASK_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names must be at least 3 letters long.\n[default]Name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(LOGIN_ASK_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretLoginAskNameTooLong() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Danidanidanidanidanidanidanidan");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(LOGIN_ASK_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names may not be longer than 30 letters.\n[default]Name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(LOGIN_ASK_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretLoginAskNameFirstCaps() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("dani");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(LOGIN_ASK_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names must begin with an upper case letter.\n[default]Name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(LOGIN_ASK_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretLoginAskNameOtherCaps() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("DAni");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(LOGIN_ASK_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names must not contain upper case letters other than the first.\n[default]Name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(LOGIN_ASK_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretAskNewYes() {
        Input input = new Input();
        Connection connection = new Connection();

        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(DEFAULT.name());

        input.setInput("y");

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[default]Please choose a name: ", result.toString());
        assertFalse(result.getSecret());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CHOOSE_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateChooseShortName() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Dan");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CHOOSE_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[default]Are you sure 'Dan' is the name you want? [y/N]: ", result.toString());
        assertFalse(result.getSecret());
        assertEquals("Dan", connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CONFIRM_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateChooseLongName() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Danidanidanidanidanidanidanida");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CHOOSE_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[default]Are you sure 'Danidanidanidanidanidanidanida' is the name you want? [y/N]: ", result.toString());
        assertFalse(result.getSecret());
        assertEquals("Danidanidanidanidanidanidanida", connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CONFIRM_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateChooseNameWhitespace() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Dani Filth");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CHOOSE_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names may not contain whitespace.\n[default]Please choose a name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CHOOSE_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateChooseNameNumbers() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Dani3");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CHOOSE_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names may only contain letters.\n[default]Please choose a name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CHOOSE_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateChooseNameTooShort() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Da");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CHOOSE_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names must be at least 3 letters long.\n[default]Please choose a name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CHOOSE_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateChooseNameTooLong() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Danidanidanidanidanidanidanidan");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CHOOSE_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names may not be longer than 30 letters.\n[default]Please choose a name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CHOOSE_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateChooseNameFirstCaps() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("dani");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CHOOSE_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names must begin with an upper case letter.\n[default]Please choose a name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CHOOSE_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateChooseNameOtherCaps() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("DAni");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CHOOSE_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]Names must not contain upper case letters other than the first.\n[default]Please choose a name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CHOOSE_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateChooseNameUserAlreadyExists() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("Dani");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CHOOSE_NAME.name());

        when(userDetailsManager.userExists(eq("Dani"))).thenReturn(true);

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[red]That name is already in use. Please try another!\n[default]Please choose a name: ", result.toString());
        assertFalse(result.getSecret());
        assertNull(connection.getName());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CHOOSE_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateConfirmNameNo() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("n");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CONFIRM_NAME.name());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("[default]Please choose a name: ", result.toString());
        assertFalse(result.getSecret());
        assertEquals(LOGIN, connection.getPrimaryState());
        assertEquals(CREATE_CHOOSE_NAME.name(), connection.getSecondaryState());
    }

    @Test
    void testInterpretCreateConfirmNameYes() {
        Input input = new Input();
        Connection connection = new Connection();

        input.setInput("y");
        connection.setName("Scion");
        connection.setPrimaryState(LOGIN);
        connection.setSecondaryState(CREATE_CONFIRM_NAME.name());
        connection.setHttpSessionId(UUID.randomUUID().toString());

        Output result = interpreter.interpret(primary, input, connection);

        assertEquals("\n[default]Dani> ", result.toString());
        assertEquals(CREATION, connection.getPrimaryState());
        assertEquals(DEFAULT.name(), connection.getSecondaryState());
    }
}
