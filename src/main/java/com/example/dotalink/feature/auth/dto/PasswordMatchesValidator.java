package com.example.dotalink.feature.auth.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegistrationForm> {

    @Override
    public boolean isValid(RegistrationForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }
        if (form.getPassword() == null || form.getConfirmPassword() == null) {
            return false;
        }
        return form.getPassword().equals(form.getConfirmPassword());
    }
}
