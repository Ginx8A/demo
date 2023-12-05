package com.globalogic.demo.config.exception;


import com.globalogic.demo.entities.ErrorDetail;

import java.util.Collections;
import java.util.List;

public class ValidatorException extends Exception {

    private final transient List<ErrorDetail> listValidator;

    public ValidatorException(List<ErrorDetail> listValidator) {
        this.listValidator = listValidator;
    }

    public List<ErrorDetail> getListValidate() {
        return Collections.unmodifiableList(listValidator);
    }

}
