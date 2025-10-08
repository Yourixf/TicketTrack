package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.note.NoteDto;
import nl.yourivb.TicketTrack.dtos.note.NoteInputDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.NoteMapper;
import nl.yourivb.TicketTrack.models.Note;
import nl.yourivb.TicketTrack.models.enums.NoteVisibility;
import nl.yourivb.TicketTrack.repositories.IncidentRepository;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
import nl.yourivb.TicketTrack.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteMapper noteMapper;
    private final NoteRepository noteRepository;
    private final InteractionRepository interactionRepository;
    private final IncidentRepository incidentRepository;

    public NoteService(NoteMapper noteMapper,
                       NoteRepository noteRepository, InteractionRepository interactionRepository, IncidentRepository incidentRepository) {
        this.noteMapper = noteMapper;
        this.noteRepository = noteRepository;
        this.interactionRepository = interactionRepository;
        this.incidentRepository = incidentRepository;
    }

    public static List<Note> filterNotesWithAccess(List<Note> notes) {
        boolean canSeeWorkNotes = SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("IT");

        List<Note> filteredNotes = notes.stream().filter(note -> note.getVisibility() == NoteVisibility.PUBLIC || canSeeWorkNotes).toList();

        return filteredNotes;
    }

    public List<NoteDto> getAllNotes() {
        List<Note> filteredNotes = filterNotesWithAccess(noteRepository.findAll().stream().toList());
        return filteredNotes.stream().map(noteMapper::toDto).toList();
    }

    public NoteDto getNoteById(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Note " + id + " not found"));
        if (note.getVisibility() == NoteVisibility.WORK &&
                !(SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("IT"))) {
            throw new AccessDeniedException("You are not allowed to view this note");
        }
        return noteMapper.toDto(note);
    }

    public NoteDto addNote(NoteInputDto noteInputDto, String noteableType, Long noteableId) {
        Note note = noteMapper.toModel(noteInputDto);

        // ensures work note integrity
        if ("WORK".equalsIgnoreCase(noteInputDto.getVisibility()) &&
                !(SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("IT"))) {
            throw new AccessDeniedException("You are not allowed to create work notes.");
        }

        switch (noteableType) {
            case "Interaction" -> {
                interactionRepository.findById(noteableId).orElseThrow(() -> new RecordNotFoundException("Interaction " + noteableId  + " not found"));
            }
            case "Incident" -> {
                incidentRepository.findById(noteableId).orElseThrow(() -> new RecordNotFoundException("Incident " + noteableId + " not found" ));
            }
            default -> throw new BadRequestException("Unsupported parent type: " + noteableType);
        }

        note.setNoteableType(noteableType);
        note.setNoteableId(noteableId);
        note.setCreatedBy(SecurityUtils.getCurrentUserDetails().getAppUser());
        noteRepository.save(note);

        return noteMapper.toDto(note);
    }

    // for admins
    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Note " + id + " not found"));

        noteRepository.deleteById(id);
    }

    public List<NoteDto> getAllNotesFromParent(String noteableType, Long noteableId) {
        boolean canSeeWorkNotes = SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("IT");

        switch (noteableType) {
            case "Interaction" -> {
                interactionRepository.findById(noteableId).orElseThrow(() -> new RecordNotFoundException("Interaction " + noteableId  + " not found"));
            }
            case "Incident" -> {
                incidentRepository.findById(noteableId).orElseThrow(() -> new RecordNotFoundException("Incident " + noteableId + " not found" ));
            }
            default -> throw new BadRequestException("Unsupported parent type: " + noteableType);
        }

        List<Note> noteList = noteRepository.findByNoteableTypeAndNoteableId(noteableType, noteableId);

        return noteMapper.toDto(filterNotesWithAccess(noteList));

    }

}
