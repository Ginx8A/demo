package com.globalogic.demo.service;

import com.globalogic.demo.config.exception.BusinessException;
import com.globalogic.demo.dto.LoginDto;

public interface LoginService {

    public LoginDto getUser(String authorization) throws BusinessException;
}
