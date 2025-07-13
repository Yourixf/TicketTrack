package nl.yourivb.TicketTrack.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExcistInDatabaseValidator.class)
public @interface ExcistInDatabase {
    String message() default "Entity with id ${validatedValue} does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // Dynamically decides what repository to use
    Class<? extends JpaRepository<?, Long>> repository();

}
