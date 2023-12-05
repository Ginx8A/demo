package com.globalogic.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class PhoneDto implements Serializable {

    private long number;
    private int citycode;
    private String contrycode;
}
