package com.globalogic.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalogic.demo.config.exception.ValidatorException;
import com.globalogic.demo.dto.SingUpDto;
import com.globalogic.demo.dto.UserDto;
import com.globalogic.demo.entities.Phone;
import com.globalogic.demo.entities.User;
import com.globalogic.demo.service.SignUpService;
import com.globalogic.demo.util.Constant;
import org.hibernate.JDBCException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SignUpController.class)
class SingUpControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SignUpService service;

    ObjectMapper mapper = new ObjectMapper();

    static SingUpDto response;

    static String email;
    static String passwordOK;

    @BeforeAll
    static void beforeAll() {
        email = "claudia.gisela.ochoa@gmail.com";
        passwordOK = "A2cd5fgh";
        UUID uuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(uuid)
                .active(Boolean.TRUE)
                .created(now)
                .lastLogin(now)
                .build();
        response = SingUpDto.builder().build();
        response.invert(user);
    }

    @Test
    void saveOk() throws Exception {
        UserDto request = UserDto.builder().email(email).password(passwordOK).build();
        when(service.save(any(UserDto.class))).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/sign-up")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    void saveWithPhonesOk() throws Exception {
        List<Phone> phones = new ArrayList<>();
        phones.add(new Phone());
        UserDto request = UserDto.builder().email(email).password(passwordOK).phones(phones).build();
        when(service.save(any(UserDto.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/sign-up")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    void saveErrorUserExists() throws Exception {
        UserDto request = UserDto.builder().email(email).password(passwordOK).build();
        when(service.save(any(UserDto.class))).thenThrow(new EntityExistsException("Ya existe un usuario con el email ingresado"));

        mockMvc.perform(MockMvcRequestBuilders.post("/sign-up")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":[{\"detail\":\"Ya existe un usuario con el email ingresado\"}]}"));
    }

    void checkValidationErrors(UserDto request, String errorDetail) throws Exception {
        String jsonContent = "{\"error\":[{\"detail\":\"".concat(errorDetail).concat("\"}]}");
        when(service.save(any(UserDto.class))).thenReturn(response);
        when(service.validatorSignUp(any(UserDto.class))).thenThrow(new ValidatorException());
        mockMvc.perform(MockMvcRequestBuilders.post("/sign-up")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void saveEmailValidationNull() throws Exception {
        UserDto request = UserDto.builder().email(email).build();
        checkValidationErrors(request, Constant.MSG_REQUIRED_PASSWORD);
    }

    @Test
    void saveEmailValidationPattern() throws Exception {
        UserDto request = UserDto.builder().email("a").password(passwordOK).build();
        checkValidationErrors(request, Constant.MSG_REGEX_MAIL);
    }

    @Test
    void savePasswordValidationNull() throws Exception {
        UserDto request = UserDto.builder().email(email).build();
        checkValidationErrors(request, Constant.MSG_REQUIRED_PASSWORD);
    }

    @Test
    void savePasswordValidationLength() throws Exception {
        UserDto request = UserDto.builder().email(email).password("zzA12").build();
        checkValidationErrors(request, Constant.MSG_REQUIRED_LENGTH);
    }

    @Test
    void savePasswordValidationUppercase() throws Exception {
        UserDto request = UserDto.builder().email(email).password("ABC12zzzz").build();
        checkValidationErrors(request, Constant.MSG_REGEX_PASSWORD);
    }

    @Test
    void savePasswordValidationNumbers() throws Exception {
        UserDto request = UserDto.builder().email(email).password("zA123zzzz").build();
        checkValidationErrors(request, Constant.MSG_REGEX_PASSWORD);
    }

    @Test
    void saveInternalServerException() throws Exception {
        UserDto request = UserDto.builder().email(email).password(passwordOK).build();
        doThrow(JDBCException.class).when(service).save(any(UserDto.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/sign-up")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}

