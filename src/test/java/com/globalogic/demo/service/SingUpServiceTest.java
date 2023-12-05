package com.globalogic.demo.service;

import com.globalogic.demo.dto.SingUpDto;
import com.globalogic.demo.dto.UserDto;
import com.globalogic.demo.entities.Phone;
import com.globalogic.demo.entities.User;
import com.globalogic.demo.repository.UserRepository;
import com.globalogic.demo.service.impl.SignUpServiceImpl;
import com.globalogic.demo.util.JwtUtils;
import org.hibernate.JDBCException;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import javax.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = SignUpService.class)
public class SingUpServiceTest {

        public static final String MY_MAIL = "mail@mail.com";
        public static final String PASSWORD = "Aa1a2zzzz";
        @InjectMocks
        SignUpServiceImpl service;

        @Mock
        UserRepository repository;

        @Mock
        JwtUtils jwtUtils;

        @Test
        void saveOk() {
            UUID uuid = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            List<Phone> phones = new ArrayList<>();
            phones.add(Phone.builder().build());

            User user = User.builder()
                    .id(uuid)
                    .active(Boolean.TRUE)
                    .created(now)
                    .lastLogin(now)
                    .email(MY_MAIL)
                    .password(PASSWORD)
                    .phones(phones)
                    .build();

            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(repository.save(any())).thenReturn(user);
            when(jwtUtils.createToken(any())).thenReturn(PASSWORD);

            UserDto dto = UserDto.builder().password(user.getPassword()).phones(phones).build();

            SingUpDto signUpDto = service.save(dto);
            assertNotNull(signUpDto);
        }

        @Test
        void saveEntityExistsException() {
            String email = "nonexistent@test.com";
            when(repository.findByEmail(email)).thenReturn(Optional.of(new User()));
            UserDto dto = UserDto.builder().email(email).build();
            EntityExistsException exception = assertThrows(
                    EntityExistsException.class,
                    () -> service.save(dto)
            );
            assertEquals("Ya existe un usuario con el email ingresado", exception.getMessage(), "NOT_EQUALS");
        }

        @Test
        void saveJDBCException() {
            when(repository.findByEmail(any())).thenThrow(JDBCException.class);
            UserDto dto = UserDto.builder().build();
            JDBCException exception = assertThrows(
                    JDBCException.class,
                    () -> service.save(dto)
            );
            assertNull(exception.getMessage());
        }

}
