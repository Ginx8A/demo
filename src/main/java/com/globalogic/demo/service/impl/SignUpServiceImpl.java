package com.globalogic.demo.service.impl;

import com.globalogic.demo.dto.SingUpDto;
import com.globalogic.demo.dto.UserDto;
import com.globalogic.demo.entities.User;
import com.globalogic.demo.repository.UserRepository;
import com.globalogic.demo.service.SignUpService;
import com.globalogic.demo.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    public static final int STRENGTH = 11;
    public static final String USER_EXIST = "Ya existe un usuario con el email ingresado";
    private final UserRepository repository;

    private final JwtUtils jwtUtils;

    @Override
    public SingUpDto save(UserDto dto) {
        validatorUser(dto);
        UUID uuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        User user = dto.convertToUser();
        user.setId(uuid);
        user.setActive(Boolean.TRUE);
        user.setCreated(now);
        user.setLastLogin(now);
        user.setPassword(encodePwd(dto.getPassword()));
        if (!CollectionUtils.isEmpty(user.getPhones())) {
            user.getPhones().forEach(phone -> {
                phone.setId(UUID.randomUUID());
                phone.setUser(user);
            });
        }
        User newUser = repository.save(user);
        SingUpDto signUpDto = SingUpDto.builder().build();
        signUpDto.invert(newUser);
        String token = jwtUtils.createToken(newUser.getEmail());
        signUpDto.setToken(token);
        return signUpDto;
    }

    private void validatorUser(UserDto dto) {
        User user = repository.findByEmail(dto.getEmail()).orElse(null);
        if (null != user) {
            throw new EntityExistsException(USER_EXIST);
        }
    }

    private String encodePwd(String string) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(STRENGTH);
        return passwordEncoder.encode(string);
    }

}
