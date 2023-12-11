package com.globalogic.demo.service;

import com.globalogic.demo.config.exception.ValidatorException;
import com.globalogic.demo.dto.SingUpDto;
import com.globalogic.demo.dto.UserDto;

public interface SignUpService {
    SingUpDto save(UserDto dto);

    void validatorSignUp(UserDto dto) throws ValidatorException;
}
