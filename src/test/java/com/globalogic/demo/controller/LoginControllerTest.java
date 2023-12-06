package com.globalogic.demo.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.globalogic.demo.config.exception.BusinessException;
import com.globalogic.demo.dto.LoginDto;
import com.globalogic.demo.service.LoginService;
import com.globalogic.demo.util.JwtUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.util.AssertionErrors.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    public static final String NOT_EQUALS = "NOT_EQUALS";
    public static final String MY_EMAIL = "claudia.gisela.ochoa@gmail.com";
    public static final String BEARER = "Bearer ";
    public static final String USUARIO_NO_EXISTE = "No existe un usuario con el email ingresado";
    public static final String USUARIO_NO_HABILITADO = "El usuario no se encuentra habilitado";

    public static final String MOCK_TOKEN_INVALID = "Bearer mock.token.invalid";
    public static final String MOCK_TOKEN_EXPIRED = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYW1hQHNhbS5zYW0iLCJleHAiOjE3MDE4MjA5NjB9.0J3nl18DBzxE7e9WdHRvj6mZDIP_vv_iwOjIxOmKCFvBMjgp0JrqbiV0FnNpe1-rSHVz0WuYTS_tIeJeDplIbg";
    public static final String MOCK_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJDbGF1ZGlhbWFpbC5jb20iLCJleHAiOjE3MDE3ODM1ODd9.M-mO23oT8EEaV76nNid7OMXGdMuMREqsUF2b5ow1UDWRq1_rSGIBVNPQcs3pmd9Y3M0hO_NCiXg0Q6mfZ70--w";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LoginService service;

    static String email;
    static String token;

    private static String createToken(String subject) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtUtils.JWT_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(JwtUtils.JWT_SECRET.getBytes()));
    }

    @BeforeAll
    static void BeforeAll() {
        email = MY_EMAIL;
        token = createToken(email);
    }

    @Test
    void loginOk() throws Exception {
        LoginDto dto = LoginDto.builder().token(token).email(email).build();
        when(service.getUser(any())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists());
    }

    @Test
    void loginOkBusinessExceptionUserExist() throws Exception {
        when(service.getUser(any())).thenThrow(new BusinessException(USUARIO_NO_EXISTE));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    @Test
    void loginOkBusinessExceptionUserInactive() throws Exception {
        when(service.getUser(any())).thenThrow(new BusinessException(USUARIO_NO_HABILITADO));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    @Test
    void loginUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginInvalidJWTFormat() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/login")
                            .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN_INVALID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
            fail("Expected JWTDecodeException was not thrown");

        } catch (JWTDecodeException e) {
            assertTrue(NOT_EQUALS, e.getMessage().contains("doesn't have a valid JSON format"));
        }
    }

    @Test
    void loginExpiredJWT() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/login")
                            .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN_EXPIRED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
            fail("Expected TokenExpiredException was not thrown");

        } catch (TokenExpiredException e) {
            assertTrue(NOT_EQUALS, e.getMessage().contains("The Token has expired"));
        }
    }

    @Test
    void loginSignatureVerificationException() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/login")
                            .header(HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
            fail("Expected SignatureVerificationException was not thrown");

        } catch (SignatureVerificationException e) {
            assertTrue(NOT_EQUALS, e.getMessage().contains("Signature resulted invalid"));
        }
    }

    @Test
    void loginMethodNotAllowedException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

}

