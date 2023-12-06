package com.globalogic.demo.controller;

import com.globalogic.demo.config.exception.ValidatorException;
import com.globalogic.demo.controller.validator.Validator;
import com.globalogic.demo.dto.SingUpDto;
import com.globalogic.demo.dto.UserDto;
import com.globalogic.demo.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class SignUpController {


    private final SignUpService service;

    @PostMapping("/sign-up")
    public ResponseEntity<SingUpDto> postSignUp(@RequestBody UserDto dto) throws ValidatorException {

        Validator.validatorSignUp(dto);
        SingUpDto response = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
