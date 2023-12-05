package com.globalogic.demo.service.impl;

import com.globalogic.demo.config.exception.BusinessException;
import com.globalogic.demo.dto.LoginDto;
import com.globalogic.demo.dto.PhoneDto;
import com.globalogic.demo.entities.Phone;
import com.globalogic.demo.entities.User;
import com.globalogic.demo.repository.PhoneRepository;
import com.globalogic.demo.repository.UserRepository;
import com.globalogic.demo.service.LoginService;
import com.globalogic.demo.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    public static final String USER_NOT_ENABLED = "Usuario NO habilitado";
    public static final String USER_NOT_EXIST = "No existe un usuario con el email ingresado";

    private final UserRepository repository;
    private final PhoneRepository phoneRepository;
    private final JwtUtils jwtUtils;

    @Override
    public LoginDto getUser(String authorizationHeader) throws BusinessException {
        LoginDto response = LoginDto.builder().build();
        String email = jwtUtils.extractSubject(authorizationHeader);
        User user = repository.findByEmail(email).orElse(null);
        if (null == user) {
            throw new BusinessException(USER_NOT_EXIST);
        }
        if (!user.isActive()) {
            throw new BusinessException(USER_NOT_ENABLED);
        }
        response.invert(user);

        List<Phone> phones = phoneRepository.findByUserId(user.getId());
        List<PhoneDto> filteredList = phones.stream()
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .map(p -> {
                    PhoneDto dto = new PhoneDto();
                    dto.setNumber(p.getNumber());
                    dto.setCitycode(p.getCitycode());
                    dto.setContrycode(p.getContrycode());
                    return dto;
                })
                .collect(Collectors.toList());

        response.setPhones(filteredList);

        String token = jwtUtils.createToken(user.getEmail());
        response.setToken(token);
        return response;
    }
}
