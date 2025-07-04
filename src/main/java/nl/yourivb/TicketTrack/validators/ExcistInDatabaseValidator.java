package nl.yourivb.TicketTrack.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

public class ExcistInDatabaseValidator implements ConstraintValidator<ExcistInDatabase, Long> {

    @Autowired
    private ApplicationContext applicationContext;

    private Class<? extends JpaRepository<?, Long>> repositoryClass;
    private JpaRepository<?, Long> repository;

    @Override
    public void initialize(ExcistInDatabase constraintAnnotation) {
        this.repositoryClass = constraintAnnotation.repository();
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        if (id == null) { return true; }

        if (repository == null) {
            repository = applicationContext.getBean(repositoryClass);
        }
        return repository.existsById(id);
    }
}
