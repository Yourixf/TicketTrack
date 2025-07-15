package nl.yourivb.TicketTrack.controllers;

import jakarta.validation.Valid;
import nl.yourivb.TicketTrack.dtos.Incident.IncidentDto;
import nl.yourivb.TicketTrack.dtos.Note.NoteDto;
import nl.yourivb.TicketTrack.dtos.Note.NoteInputDto;
import nl.yourivb.TicketTrack.dtos.attachment.AttachmentDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionInputDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionPatchDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.AttachmentService;
import nl.yourivb.TicketTrack.services.IncidentService;
import nl.yourivb.TicketTrack.services.InteractionService;
import nl.yourivb.TicketTrack.services.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
public class InteractionController {

    private final InteractionService interactionService;
    private final NoteService noteService;
    private final AttachmentService attachmentService;
    private final IncidentService incidentService;

    public InteractionController(InteractionService interactionService, NoteService noteService, AttachmentService attachmentService, IncidentService incidentService) {
        this.interactionService = interactionService;
        this.noteService = noteService;
        this.attachmentService = attachmentService;
        this.incidentService = incidentService;
    }

    @GetMapping("/interactions")
    public ResponseEntity<ApiResponse<List<InteractionDto>>>  getAllInteractions() {
        List<InteractionDto> dtos;

        dtos = interactionService.getAllInteractions();

        return new ResponseEntity<>(
                new ApiResponse<>("Interactions fetched", HttpStatus.OK, dtos),
                HttpStatus.OK
        );
    }

    @GetMapping("/interactions/{id}")
    public ResponseEntity<ApiResponse<InteractionDto>> getInteractionById(@PathVariable Long id){
        InteractionDto interaction = interactionService.getInteractionById(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched interaction " + id, HttpStatus.OK, interaction),
                HttpStatus.OK
        );
    }

    @PostMapping("/interactions")
    public ResponseEntity<ApiResponse<InteractionDto>> addInteraction(@Valid @RequestBody InteractionInputDto interactionInputDto) {
        InteractionDto dto = interactionService.addInteraction(interactionInputDto);
        URI uri = URI.create("/interactions/" + dto.getId());

        return ResponseEntity.created(uri).body(new ApiResponse<>("Created interaction", HttpStatus.CREATED, dto));
    }

    @PutMapping("/interactions/{id}")
    public ResponseEntity<ApiResponse<InteractionDto>> updateInteraction(@PathVariable Long id, @Valid @RequestBody InteractionInputDto newInteraction) {
        InteractionDto updatedInteraction = interactionService.updateInteraction(id, newInteraction);

        return new ResponseEntity<>(
                new ApiResponse<>("Interaction updated", HttpStatus.OK, updatedInteraction), HttpStatus.OK
        );
    }

    @PatchMapping("/interactions/{id}")
    public ResponseEntity<ApiResponse<InteractionDto>> patchInteraction(@PathVariable Long id, @Valid @RequestBody InteractionPatchDto patchedInteraction) {
        InteractionDto updatedInteraction = interactionService.patchInteraction(id, patchedInteraction);

        return new ResponseEntity<>(
                new ApiResponse<>("Interaction updated", HttpStatus.OK, updatedInteraction), HttpStatus.OK
        );
    }

    @DeleteMapping("/interactions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInteraction(@PathVariable Long id) {
        interactionService.deleteInteraction(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Interaction deleted", HttpStatus.OK, null),
                HttpStatus.OK
        );
    }

    @PostMapping("/interactions/{id}/notes")
    public ResponseEntity<ApiResponse<InteractionDto>> addNote(@PathVariable Long id, @Valid @RequestBody NoteInputDto noteInputDto) {
        NoteDto note = noteService.addNote(noteInputDto, "Interaction", id);

        InteractionDto interaction = interactionService.getInteractionById(id);

        URI uri = URI.create("/notes/" + note.getId());

        return ResponseEntity.created(uri).body(new ApiResponse<>("Added note", HttpStatus.CREATED, interaction));
    }

    @PostMapping("/interactions/{id}/attachments")
    public ResponseEntity<ApiResponse<AttachmentDto>> addAttachment(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        AttachmentDto attachment = attachmentService.addAttachment(file, "Interaction", id);

        URI uri = URI.create("/attachments/" + attachment.getId());
        return ResponseEntity.created(uri).body(new ApiResponse<>("Added attachment", HttpStatus.CREATED, attachment));
    }

    @PostMapping("/interactions/{id}/escalate")
    public ResponseEntity<ApiResponse<IncidentDto>> escalateFromInteraction(@PathVariable Long id) {
        IncidentDto incident = incidentService.escalateFromInteraction(id);

        URI uri = URI.create("/incidents/" + incident.getId());

        return ResponseEntity.created(uri).body(new ApiResponse<>("Escalated interaction to incident", HttpStatus.CREATED, incident));
    }
}
