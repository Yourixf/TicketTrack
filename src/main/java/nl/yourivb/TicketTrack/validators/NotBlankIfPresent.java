package nl.yourivb.TicketTrack.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankIfPresentValidator.class)
public @interface NotBlankIfPresent {
    String message() default "Field can't be blank";
    Class<?>[] groups() default {};
    Class<? extends Payload> [] payload() default {};
}
