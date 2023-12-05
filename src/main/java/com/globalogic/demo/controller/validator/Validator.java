package com.globalogic.demo.controller.validator;

import com.globalogic.demo.config.exception.ValidatorException;
import com.globalogic.demo.dto.UserDto;
import com.globalogic.demo.entities.ErrorDetail;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class Validator {

    private static final String MAIL = "mail";
    private static final String PASSWORD = "password";
    private static final String REGEX_MAIL = "^[A-Za-z0-9+_.-]{3,}+@[A-Za-z0-9-]{2,}+.[A-Za-z0-9-]{2,}$";
    private static final String REGEX_PASSWORD_MAYUSC = "[A-Z]";
    private static final String REGEX_PASSWORD_NUM = "\\d";
    public static final String MSG_REQUIRED_MAIL = "Email es un campo requerido. No puede ser nulo";
    public static final String MSG_REQUIRED_PASSWORD = "Password es un campo requerido. No puede ser nulo";
    public static final String MSG_REQUIRED_LENGTH = "Password debe tener entre 12 y 8 caracteres";

    public static final String MSG_REGEX_MAIL = "Email no tiene un formato valido";
    public static final String MSG_REGEX_PASSWORD = "Password debe tener una letra mayúscula y dos números, en combinación de letras minúsculas";
    private static final String MAIL_REGEX = "mailRegex";
    private static final String PASSWORD_REGEX = "passwordRegex";
    private static final String PASSWORD_LENGTH = "passwordLength";
    private static final int MIN_LENGTH_PASSWORD = 8;
    private static final int MAX_LENGTH_PASSWORD = 12;
    private static final String REGEX = "Regex";

    private Validator() {

    }

    public static void validate(BindingResult result) throws ValidatorException {
        List<ErrorDetail> listValidate = new ArrayList<>();
        result.getFieldErrors().forEach(er -> listValidate.add(ErrorDetail.builder().detail(er.getDefaultMessage()).build()));
        throw new ValidatorException(listValidate);
    }


    public static void validatorSignUp(UserDto dto) throws ValidatorException {
        List<ErrorDetail> listError = new ArrayList<>();
        validateGeneric(dto.getEmail(), MAIL, listError);
        validateGeneric(dto.getPassword(), PASSWORD, listError);
        if (StringUtils.isNotBlank(dto.getPassword())
                && (dto.getPassword().length() < MIN_LENGTH_PASSWORD
                || dto.getPassword().length() > MAX_LENGTH_PASSWORD)) {
            listError.add(createErrorDetail(PASSWORD_LENGTH));
        }
        if (!CollectionUtils.isEmpty(listError)) {
            throw new ValidatorException(listError);
        }
    }

    private static void validateGeneric(String value, String type, List<ErrorDetail> listError) {
        if (StringUtils.isNotBlank(value)) {
            switch (type) {
                case PASSWORD:
                    if (notMatchesRegex(REGEX_PASSWORD_MAYUSC, value, 1)
                            || notMatchesRegex(REGEX_PASSWORD_NUM, value, 2)) {
                        listError.add(createErrorDetail(type.concat(REGEX)));
                    }
                    break;
                case MAIL:
                    if (!Pattern.matches(REGEX_MAIL, value)) {
                        listError.add(createErrorDetail(type.concat(REGEX)));
                    }
                    break;
                default:
                    break;
            }
        } else {
            listError.add(createErrorDetail(type));
        }
    }

    private static ErrorDetail createErrorDetail(String tipError) {
        String detail = "";
        switch (tipError) {
                case MAIL:
                    detail = MSG_REQUIRED_MAIL;
                    break;
                case MAIL_REGEX:
                    detail = MSG_REGEX_MAIL;
                    break;
                case PASSWORD:
                    detail = MSG_REQUIRED_PASSWORD;
                    break;
                case PASSWORD_REGEX:
                    detail = MSG_REGEX_PASSWORD;
                    break;
                case PASSWORD_LENGTH:
                    detail = MSG_REQUIRED_LENGTH;
                    break;
                default:
                    break;
        }
        return ErrorDetail.builder().detail(detail).build();
    }


    private static boolean notMatchesRegex(String regex, String value, int sizeValue) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);

        int count = 0;
        while (matcher.find()) {
            count++;
            if (count > sizeValue) {
                return true;
            }
        }
        return false;
    }
}
