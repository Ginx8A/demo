package com.globalogic.demo.util;


public class Constant {

    private Constant() {
    }

    public static final String MAIL = "mail";
    public static final String PASSWORD = "password";
    public static final String REGEX_MAIL = "^[A-Za-z0-9+_.-]{3,}+@[A-Za-z0-9-]{2,}+.[A-Za-z0-9-]{2,}$";
    public static final String REGEX_PASSWORD_MAYUSC = "[A-Z]";
    public static final String REGEX_PASSWORD_NUM = "\\d";
    public static final String MSG_REQUIRED_MAIL = "Email es un campo requerido. No puede ser nulo";
    public static final String MSG_REQUIRED_PASSWORD = "Password es un campo requerido. No puede ser nulo";
    public static final String MSG_REQUIRED_LENGTH = "Password debe tener entre 12 y 8 caracteres";

    public static final String MSG_REGEX_MAIL = "Email no tiene un formato valido";
    public static final String MSG_REGEX_PASSWORD = "Password debe tener una letra mayúscula y dos números, en combinación de letras minúsculas";
    public static final String MAIL_REGEX = "mailRegex";
    public static final String PASSWORD_REGEX = "passwordRegex";
    public static final String PASSWORD_LENGTH = "passwordLength";
    public static final int MIN_LENGTH_PASSWORD = 8;
    public static final int MAX_LENGTH_PASSWORD = 12;
    public static final String REGEX = "Regex";

    public static final int STRENGTH = 11;
    public static final String USER_EXIST = "Ya existe un usuario con el email ingresado";

}
