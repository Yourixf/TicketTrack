package nl.yourivb.TicketTrack.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExcistInDatabaseValidator.class)
public @interface ExcistInDatabase {
    String message() default "Entity with id ${validatedValue} does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // Dynamisch meegeven welke repository gebruikt moet worden
    Class<? extends JpaRepository<?, Long>> repository();

}
