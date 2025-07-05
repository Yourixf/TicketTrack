package nl.yourivb.TicketTrack.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class NotBlankIfPresentValidator implements ConstraintValidator<NotBlankIfPresent, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !value.trim().isEmpty();
    }
}
