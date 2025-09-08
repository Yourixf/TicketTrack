package nl.yourivb.TicketTrack.controllers;

import nl.yourivb.TicketTrack.dtos.attachment.AttachmentDownloadDto;
import nl.yourivb.TicketTrack.dtos.attachment.AttachmentDto;
import nl.yourivb.TicketTrack.mappers.AttachmentMapper;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import nl.yourivb.TicketTrack.services.AttachmentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttachmentDto>>> getAllAttachments () {
        List<AttachmentDto> dtos;

        dtos = attachmentService.getAllAttachments();

        return new ResponseEntity<>(
                new ApiResponse<>("Attachments fetched", HttpStatus.OK, dtos), HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttachmentDto>> getAttachmentById(@PathVariable Long id) {
        AttachmentDto attachment = attachmentService.getAttachmentById(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched attachment " + id, HttpStatus.OK, attachment), HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachmentFromAllParents(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Attachment deleted", HttpStatus.OK, null), HttpStatus.OK
        );
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadAttachment(@PathVariable Long id) {
        AttachmentDownloadDto download = attachmentService.downloadAttachment(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.getFileName() + "\"")
                .body(download.getFile());
    }
}
