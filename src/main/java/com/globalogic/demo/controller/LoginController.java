package com.globalogic.demo.controller;

import com.globalogic.demo.config.exception.BusinessException;
import com.globalogic.demo.dto.LoginDto;
import com.globalogic.demo.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public ResponseEntity<Object> getLogin(@RequestHeader("Authorization") String authorization) throws BusinessException {
        LoginDto response = loginService.getUser(authorization);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
