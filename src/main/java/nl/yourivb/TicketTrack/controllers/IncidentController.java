package nl.yourivb.TicketTrack.controllers;

import jakarta.validation.Valid;
import nl.yourivb.TicketTrack.dtos.Incident.IncidentDto;
import nl.yourivb.TicketTrack.dtos.Incident.IncidentInputDto;
import nl.yourivb.TicketTrack.dtos.Incident.IncidentPatchDto;
import nl.yourivb.TicketTrack.dtos.Note.NoteDto;
import nl.yourivb.TicketTrack.dtos.Note.NoteInputDto;
import nl.yourivb.TicketTrack.dtos.attachment.AttachmentDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.AttachmentService;
import nl.yourivb.TicketTrack.services.IncidentService;
import nl.yourivb.TicketTrack.services.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final NoteService noteService;
    private final AttachmentService attachmentService;

    public IncidentController(IncidentService incidentService, NoteService noteService, AttachmentService attachmentService) {
        this.incidentService = incidentService;
        this.noteService = noteService;
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IncidentDto>>> getAllIncidents() {
        List<IncidentDto> dtos;

        dtos = incidentService.getAllIncidents();

        return new ResponseEntity<>(
                new ApiResponse<>("Incidents fetched", HttpStatus.OK, dtos), HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentDto>> getIncidentById(@PathVariable Long id) {
        IncidentDto incident = incidentService.getIncidentById(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched incident " + id, HttpStatus.OK, incident), HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentDto>> updateIncident(@PathVariable Long id, @Valid @RequestBody IncidentInputDto newIncident) {
        IncidentDto updatedIncident = incidentService.updateIncident(id, newIncident);

        return new ResponseEntity<>(
                new ApiResponse<>("Incident updated", HttpStatus.OK, updatedIncident), HttpStatus.OK
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentDto>> patchIncident(@PathVariable Long id, @Valid @RequestBody IncidentPatchDto patchedIncident) {
        IncidentDto updatedIncident = incidentService.patchIncident(id, patchedIncident);

        return new ResponseEntity<>(
                new ApiResponse<>("Incident updated", HttpStatus.OK, updatedIncident), HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Incident deleted", HttpStatus.OK, null),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<ApiResponse<List<NoteDto>>> getIncidentNotes(@PathVariable Long id) {
        List<NoteDto> dtos = noteService.getAllNotesFromParent("Incident", id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched notes from incident " + id, HttpStatus.OK, dtos), HttpStatus.OK
        );
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<ApiResponse<NoteDto>> addNote(@PathVariable Long id, @Valid @RequestBody NoteInputDto noteInputDto) {
        NoteDto note = noteService.addNote(noteInputDto, "Incident", id);

        URI uri = URI.create("/notes/" + note.getId());
        return ResponseEntity.created(uri).body(new ApiResponse<>("Added note", HttpStatus.CREATED, note));
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<ApiResponse<List<AttachmentDto>>> getInteractionAttachments(@PathVariable Long id) {
        List<AttachmentDto> dtos = attachmentService.getAllAttachmentsFromParent("Incident", id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched attachments from incident " + id, HttpStatus.OK, dtos), HttpStatus.OK
        );
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<ApiResponse<AttachmentDto>> addAttachment(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        AttachmentDto attachment = attachmentService.addAttachment(file, "Incident", id);

        URI uri = URI.create("/attachments/" + attachment.getId());
        return ResponseEntity.created(uri).body(new ApiResponse<>("Added attachment", HttpStatus.CREATED, attachment));
    }
}
