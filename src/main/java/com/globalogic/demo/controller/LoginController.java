package com.globalogic.demo.controller;

import com.globalogic.demo.config.exception.BusinessException;
import com.globalogic.demo.config.exception.ValidatorException;
import com.globalogic.demo.controller.validator.Validator;
import com.globalogic.demo.dto.LoginDto;
import com.globalogic.demo.dto.SingUpDto;
import com.globalogic.demo.dto.UserDto;
import com.globalogic.demo.service.LoginService;
import com.globalogic.demo.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {


    private final SignUpService service;
    private final LoginService loginService;

    @PostMapping("/test")
    public ResponseEntity<Object> getTest() {
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }


    @PostMapping("/sign-up")
    public ResponseEntity<SingUpDto> postSignUp(@RequestBody UserDto dto) throws ValidatorException {

        Validator.validatorSignUp(dto);
        SingUpDto response = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> getLogin(@RequestHeader("Authorization") String authorization) throws BusinessException {
        LoginDto response = loginService.getUser(authorization);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
