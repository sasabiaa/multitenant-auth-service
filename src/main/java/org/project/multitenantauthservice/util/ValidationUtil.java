package org.project.multitenantauthservice.util;

public class ValidationUtil {

    public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\w\\W]{8,}$";
    public static final String REGEX_USERNAME = "^[a-zA-Z][a-zA-Z0-9_-]{2,15}$";

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches(REGEX_EMAIL);
    }

    public static boolean isValidPassword(String email) {
        if (email == null) return false;
        return email.matches(REGEX_PASSWORD);
    }

    public static boolean isValidUsername(String email) {
        if (email == null) return false;
        return email.matches(REGEX_USERNAME);
    }
}
