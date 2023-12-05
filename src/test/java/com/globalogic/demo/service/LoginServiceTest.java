package com.globalogic.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.globalogic.demo.config.exception.BusinessException;
import com.globalogic.demo.dto.LoginDto;
import com.globalogic.demo.entities.Phone;
import com.globalogic.demo.entities.User;
import com.globalogic.demo.repository.PhoneRepository;
import com.globalogic.demo.repository.UserRepository;
import com.globalogic.demo.service.impl.LoginServiceImpl;
import com.globalogic.demo.util.JwtUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = LoginService.class)
public class LoginServiceTest {

    public static final String USUARIO_NO_EXISTE = "No existe un usuario con el email ingresado";
    public static final String USUARIO_NO_HABILITADO = "El usuario no se encuentra habilitado";

    @InjectMocks
    LoginServiceImpl service;

    @Mock
    UserRepository repository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    PhoneRepository phoneRepository;

    static String email;
    static String token;

    private static String createToken(String subject) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + 30000))
                .sign(Algorithm.HMAC512(JwtUtils.JWT_SECRET.getBytes()));
    }

    @BeforeAll
    static void BeforeAll() {
        email = "claudia.gisela.ochoa@gmail.com";
        token = createToken(email);
    }

    @Test
    void getUserOk() throws Exception {
        UUID uuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        List<Phone> phones = new ArrayList<>();
        phones.add(Phone.builder().user(User.builder().id(uuid).build()).build());

        User user = User.builder()
                .id(uuid)
                .active(Boolean.TRUE)
                .created(now)
                .lastLogin(now)
                .phones(phones)
                .build();

        when(jwtUtils.extractSubject(token)).thenReturn(email);
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(phoneRepository.findByUserId(any())).thenReturn(phones);
        LoginDto response = service.getUser(token);
        assertNotNull(response);
    }

    @Test
    void userNotExist() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.getUser(token)
        );
        assertEquals(USUARIO_NO_EXISTE, exception.getMessage());
    }

    @Test
    void userInactive() {
        when(jwtUtils.extractSubject(token)).thenReturn(email);
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.getUser(token)
        );
        assertEquals(USUARIO_NO_HABILITADO, exception.getMessage());
    }

}

