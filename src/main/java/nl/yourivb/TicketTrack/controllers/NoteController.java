package nl.yourivb.TicketTrack.controllers;

import nl.yourivb.TicketTrack.dtos.Note.NoteDto;
import nl.yourivb.TicketTrack.dtos.Note.NoteInputDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Note deleted", HttpStatus.OK, null),
                HttpStatus.OK
        );
    }

    @PutMapping("/notes/{id}")
    public ResponseEntity<ApiResponse<NoteDto>> updateNote(@PathVariable Long id, @RequestBody NoteInputDto newNote) {
        NoteDto updateNote = noteService.updateNote(id, newNote);

        return new ResponseEntity<>(
                new ApiResponse<>("Note updated", HttpStatus.OK, updateNote), HttpStatus.OK
        );
    }
}
