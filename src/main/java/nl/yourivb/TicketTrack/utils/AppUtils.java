package nl.yourivb.TicketTrack.utils;

import nl.yourivb.TicketTrack.models.*;
import nl.yourivb.TicketTrack.models.enums.NoteVisibility;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
import nl.yourivb.TicketTrack.security.SecurityUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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

    // makes a list of ids of a entity list.
    public static <T> List<Long> extractIds(List<T> entities, Function<T, Long> idGetter) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(idGetter)
                .filter(Objects::nonNull)
                .toList();
    }

    // this method gets and sets the note and attachments from parent entity.
    public static <T> void enrichWithRelations(
            T parent,
            String parentType,
            Long parentId,
            NoteRepository noteRepository,
            AttachmentRepository attachmentRepository) {

        // makes sure only allowed roles/employees see their notes.
        boolean canSeeWorkNotes = SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("IT");

        List<Note> notes = noteRepository.findByNoteableTypeAndNoteableId(parentType, parentId)
                .stream()
                .filter(note -> note.getVisibility() == NoteVisibility.PUBLIC || canSeeWorkNotes)
                .toList();

        List<Attachment> attachments = attachmentRepository.findByAttachableTypeAndAttachableId(parentType, parentId);

        if (parent instanceof Incident incident) {
            incident.setNotes(notes);
            incident.setAttachments(attachments);
        } else if (parent instanceof Interaction interaction) {
            interaction.setNotes(notes);
            interaction.setAttachments(attachments);
        }
    }

    public static void validateTicketAccess(AppUser openedBy, AppUser openedFor) {
        AppUser currentUser = SecurityUtils.getCurrentUserDetails().getAppUser();
        System.out.println(currentUser);
        System.out.println(SecurityUtils.getCurrentUserDetails());
        if (SecurityUtils.hasRole("CUSTOMER")) {
            boolean isOwner = (openedBy != null && currentUser.equals(openedBy))
                    || (openedFor != null && currentUser.equals(openedFor));

            if (!isOwner) {
                throw new AccessDeniedException("You have no permission to view this record.");
            }
        }
    }
}
