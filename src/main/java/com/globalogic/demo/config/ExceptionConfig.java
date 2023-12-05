package com.globalogic.demo.config;

import com.globalogic.demo.config.exception.BusinessException;
import com.globalogic.demo.config.exception.ValidatorException;
import com.globalogic.demo.entities.ErrorDetail;
import com.globalogic.demo.entities.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityExistsException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionConfig {

    @ExceptionHandler({
            BusinessException.class,
            EntityExistsException.class,
            ValidatorException.class,
            Exception.class
    })
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (ex instanceof BusinessException
                || ex instanceof EntityExistsException) {
            return handleGenericException(ex, timestamp, HttpStatus.BAD_REQUEST);
        }
        if (ex instanceof ValidatorException) {
            return handleValidationException((ValidatorException) ex, timestamp);
        }
        return handleGenericException(ex, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> handleGenericException(Exception ex, Timestamp timestamp, HttpStatus httpStatus) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        errorDetails.add(ErrorDetail.builder().codigo(httpStatus.value()).detail(ex.getLocalizedMessage()).timestamp(timestamp).build());
        ErrorResponse errorResponse = ErrorResponse.builder().error(errorDetails).build();
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    private ResponseEntity<ErrorResponse> handleValidationException(ValidatorException ex, Timestamp timestamp) {
        HttpStatus statusCode = HttpStatus.BAD_REQUEST;
        List<ErrorDetail> details = ex.getListValidate();

        details.forEach(detail -> {
            detail.setCodigo(statusCode.value());
            detail.setTimestamp(timestamp);
        });

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}

