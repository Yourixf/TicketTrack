package nl.yourivb.TicketTrack.utils;

import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;

public class AppUtils {

    // checks if there are any fields in the patch body
    public static boolean allFieldsNull(Object dto) {
        if (dto == null) return true;

        for (Field field : dto.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(dto) != null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to access field", e);
            }
        }
        return true;
    }

    // generates a registration number based on arguments
    public static String generateRegistrationNumber(String registrationType, JpaRepository<?, Long> repository) {
        Long count = repository.count();
        Long nextNumber = count + 1;
        return String.format("%s%07d", registrationType.toUpperCase(), nextNumber);
    }
}
