package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.note.NoteDto;
import nl.yourivb.TicketTrack.dtos.note.NoteInputDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.NoteMapper;
import nl.yourivb.TicketTrack.models.Incident;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.models.Note;
import nl.yourivb.TicketTrack.models.enums.NoteVisibility;
import nl.yourivb.TicketTrack.repositories.IncidentRepository;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
import nl.yourivb.TicketTrack.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class NoteService {

    private final NoteMapper noteMapper;
    private final NoteRepository noteRepository;
    private final InteractionRepository interactionRepository;
    private final IncidentRepository incidentRepository;

    public NoteService(NoteMapper noteMapper,NoteRepository noteRepository,
                       InteractionRepository interactionRepository, IncidentRepository incidentRepository) {
        this.noteMapper = noteMapper;
        this.noteRepository = noteRepository;
        this.interactionRepository = interactionRepository;
        this.incidentRepository = incidentRepository;
    }

    public static List<Note> filterNotesWithVisibilityAccess(List<Note> notes) {
        boolean canSeeWorkNotes = SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("IT");

        List<Note> filteredNotes = notes.stream().filter(note -> note.getVisibility() == NoteVisibility.PUBLIC || canSeeWorkNotes).toList();

        return filteredNotes;
    }

    private void validateAccessToParent(String noteableType, Long noteableId) {
        final Long currentUserId = SecurityUtils.getCurrentUserId();
        final boolean hasElevatedRights = SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("IT");
        if (hasElevatedRights) return;

        switch (noteableType) {
            case "Interaction" -> {
                Interaction interaction = interactionRepository.findById(noteableId)
                        .orElseThrow(() -> new RecordNotFoundException("Interaction " + noteableId + " not found"));

                boolean isParty = Objects.equals(interaction.getOpenedBy().getId(), currentUserId)
                        || Objects.equals(interaction.getOpenedFor().getId(), currentUserId);

                if (!isParty) throw new AccessDeniedException("You have no permission to access or alter this interaction");
            }
            case "Incident" -> {
                Incident incident = incidentRepository.findById(noteableId)
                        .orElseThrow(() -> new RecordNotFoundException("Incident " + noteableId + " not found"));

                boolean isParty = Objects.equals(incident.getOpenedBy().getId(), currentUserId)
                        || Objects.equals(incident.getOpenedFor().getId(), currentUserId);

                if (!isParty) throw new AccessDeniedException("You have no permission to access or alter this incident");
            }
            default -> throw new BadRequestException("Unsupported parent type: " + noteableType);
        }
    }

    private void validateVisibilityAccess(Note note) {
        boolean hasElevatedRights = SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("IT");

        NoteVisibility visibility = note.getVisibility();

        if (visibility == NoteVisibility.WORK && !hasElevatedRights) {
            throw new AccessDeniedException("You have no permission for WORK notes");
        }
    }

    public List<NoteDto> getAllNotes() {
        List<Note> filteredNotes = filterNotesWithVisibilityAccess(noteRepository.findAll()
                .stream()
                .filter(note -> {
                    try {
                        validateAccessToParent(note.getNoteableType(), note.getNoteableId());
                        validateVisibilityAccess(note);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList());
        return filteredNotes.stream().map(noteMapper::toDto).toList();
    }

    public NoteDto getNoteById(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Note " + id + " not found"));

        validateAccessToParent(note.getNoteableType(), note.getNoteableId());
        validateVisibilityAccess(note);

        return noteMapper.toDto(note);
    }

    public NoteDto addNote(NoteInputDto noteInputDto, String noteableType, Long noteableId) {
        Note note = noteMapper.toModel(noteInputDto);

        validateAccessToParent(noteableType, noteableId);

        note.setNoteableType(noteableType);
        note.setNoteableId(noteableId);
        note.setCreatedBy(SecurityUtils.getCurrentUserDetails().getAppUser());

        validateVisibilityAccess(note);

        noteRepository.save(note);

        return noteMapper.toDto(note);
    }

    // for admins
    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Note " + id + " not found"));

        validateAccessToParent(note.getNoteableType(), note.getNoteableId());
        validateVisibilityAccess(note);

        noteRepository.deleteById(id);
    }

    public List<NoteDto> getAllNotesFromParent(String noteableType, Long noteableId) {
        validateAccessToParent(noteableType, noteableId);

        List<Note> noteList = noteRepository.findByNoteableTypeAndNoteableId(noteableType, noteableId);

        return noteMapper.toDto(filterNotesWithVisibilityAccess(noteList));

    }
}
