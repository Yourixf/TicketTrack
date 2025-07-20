package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.Note.NoteDto;
import nl.yourivb.TicketTrack.dtos.Note.NoteInputDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.NoteMapper;
import nl.yourivb.TicketTrack.models.Note;
import nl.yourivb.TicketTrack.repositories.IncidentRepository;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
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

    public List<NoteDto> getAllNotes() {
        return noteRepository.findAll().stream().map(noteMapper::toDto).toList();
    }

    public NoteDto getNoteById(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Note " + id + " not found"));

        return noteMapper.toDto(note);
    }

    public NoteDto addNote(NoteInputDto noteInputDto, String noteableType, Long parentId) {
        Note note = noteMapper.toModel(noteInputDto);

        switch (noteableType) {
            case "Interaction" -> {
                interactionRepository.findById(parentId).orElseThrow(() -> new RecordNotFoundException("Interaction " + parentId  + " not found"));
            }
            case "Incident" -> {
                incidentRepository.findById(parentId).orElseThrow(() -> new RecordNotFoundException("Incident " + parentId + " not found" ));
            }
            default -> throw new BadRequestException("Unsupported parent type: " + noteableType);
        }

        note.setNoteableType(noteableType);
        note.setNoteableId(parentId);
        noteRepository.save(note);

        return noteMapper.toDto(note);
    }

    // for admins
    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Note " + id + " not found"));

        noteRepository.deleteById(id);
    }

}
