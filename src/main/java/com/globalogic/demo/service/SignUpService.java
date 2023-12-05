package com.globalogic.demo.service;

import com.globalogic.demo.dto.SingUpDto;
import com.globalogic.demo.dto.UserDto;

public interface SignUpService {
    SingUpDto save(UserDto dto);

}
