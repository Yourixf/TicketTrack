package nl.yourivb.TicketTrack.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionInputDto;
import nl.yourivb.TicketTrack.security.AppUserDetails;
import nl.yourivb.TicketTrack.security.SecurityUtils;

public class InteractionInputValidator implements ConstraintValidator<ValidInteractionInput, InteractionInputDto> {

    @Override
    public boolean isValid(InteractionInputDto dto, ConstraintValidatorContext context) {
        AppUserDetails userDetails = SecurityUtils.getCurrentUserDetails();
        String role = userDetails.getAppUser().getRole().getName();

        boolean valid = true;

        // to handle role specific field validation
        if (!"CUSTOMER".equals(role)) {
            if (dto.getChannel() == null) {
                context.buildConstraintViolationWithTemplate("Must not be null")
                        .addPropertyNode("channel")
                        .addConstraintViolation();
                valid = false;
            }
            if (dto.getCategory() == null) {
                context.buildConstraintViolationWithTemplate("Must not be null")
                        .addPropertyNode("category")
                        .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}
