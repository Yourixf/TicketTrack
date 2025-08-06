package nl.yourivb.TicketTrack.utils;

import nl.yourivb.TicketTrack.dtos.Incident.IncidentDto;
import nl.yourivb.TicketTrack.models.Attachment;
import nl.yourivb.TicketTrack.models.Note;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
import org.springframework.data.jpa.repository.JpaRepository;

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

    // TODO fix this mess

//
//    public static <T> void addPolyRelationEntities (T dto,
//                                                     Long id,
//                                                     String table,
//                                                     NoteRepository noteRepository,
//                                                     AttachmentRepository attachmentRepository) {
//
//        var notes = noteRepository.findByNoteableTypeAndNoteableId(table, id);
//        dto.setNoteIds(AppUtils.extractIds(notes, Note::getId));
//
//        var attachments = attachmentRepository.findByAttachableTypeAndAttachableId(table, id);
//        dto.setAttachmentIds(AppUtils.extractIds(attachments, Attachment::getId));
//    }

}
