package nl.yourivb.TicketTrack.controllers;

import nl.yourivb.TicketTrack.dtos.Note.NoteDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/notes")
    public ResponseEntity<ApiResponse<List<NoteDto>>> getAllNotes() {
        List<NoteDto> dtos;

        dtos = noteService.getAllNotes();

        return new ResponseEntity<>(
                new ApiResponse<>("Notes fetched", HttpStatus.OK, dtos),
                HttpStatus.OK
        );
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<ApiResponse<NoteDto>> getNoteById(@PathVariable Long id) {
        NoteDto note = noteService.getNoteById(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched note " + id, HttpStatus.OK, note), HttpStatus.OK
        );
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Note deleted", HttpStatus.OK, null),
                HttpStatus.OK
        );
    }
}
