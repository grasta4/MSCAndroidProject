package com.example.msc.ui.util;

interface Constants {
    int MIN_PWD_LEN = 8, MAX_PWD_LEN = 16, MIN_UNAME_LEN = 3, MAX_UNAME_LEN = 10;
}

public enum Validator {
    MAIL_INVALID("Email address format not valid..."),
    PWD_LEN("Password length must be between " + Constants.MIN_PWD_LEN + " and " + Constants.MAX_PWD_LEN + " characters!"),
    PWD_NOT_MATCHING("Passwords not matching..."),
    UNAME_LEN("Username length must be between " + Constants.MIN_UNAME_LEN + " and " + Constants.MAX_UNAME_LEN + " characters..."),
    UNAME_CHARS_INVALID("Only letters, numbers and _ are allowed for username");

    private final String message;

    Validator(final String message) {
        this.message = message;
    }

    public static String validateAll(final String username, final String password, final String passwordConfirm, final String email) {
        String tmp;

        return (tmp = validateUsername(username)) != null || (tmp = validatePassword(password, passwordConfirm)) != null || (tmp = validateEmail(email)) != null ? tmp : null;
    }

    public static String validateEmail(final String email) {
        return !email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$") ? MAIL_INVALID.getMessage() : null;
    }

    public static String validatePassword(final String password, final String confirm) {
        if(password == null || password.length() < Constants.MIN_PWD_LEN || password.length() > Constants.MAX_UNAME_LEN)
            return PWD_LEN.getMessage();
        if(!password.equals(confirm))
            return PWD_NOT_MATCHING.getMessage();

        return null;
    }

    public static String validateUsername(final String username) {
        if(username == null || username.length() < Constants.MIN_UNAME_LEN || username.length() > Constants.MAX_UNAME_LEN)
            return UNAME_LEN.getMessage();
        if(!username.matches("^[a-zA-Z0-9_]+$"))
            return UNAME_CHARS_INVALID.getMessage();

        return null;
    }

    public String getMessage() {
        return this.message;
    }
}
