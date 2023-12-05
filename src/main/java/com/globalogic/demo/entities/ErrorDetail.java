package com.globalogic.demo.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@Builder
public class ErrorDetail {

    private Timestamp timestamp;
    private int codigo;
    private String detail;

}
