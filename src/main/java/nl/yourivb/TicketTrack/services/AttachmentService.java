package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.attachment.AttachmentDto;
import nl.yourivb.TicketTrack.mappers.AttachmentMapper;
import nl.yourivb.TicketTrack.models.Attachment;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;

    public AttachmentService(AttachmentRepository attachmentRepository,
                             AttachmentMapper attachmentMapper) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentMapper = attachmentMapper;
    }

    public AttachmentDto addAttachment(MultipartFile file, String attachableType, Long attachableId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty file is not allowed");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = FilenameUtils.getExtension(fileName);
        String filePath = "attachments/" + UUID.randomUUID() + "." + fileExtension;

        // Sla bestand fysiek op
        Path targetLocation = Paths.get("src/main/resources/uploads").resolve(filePath);
        try {
            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation);
        } catch (IOException e) {
            throw new RuntimeException("Fout bij opslaan bestand", e);
        }

        // Maak Attachment entity aan
        Attachment attachment = new Attachment();
        attachment.setFileType(file.getContentType());
        attachment.setFileName(fileName);
        attachment.setFilePath(filePath);
        attachment.setAttachableType(attachableType);
        attachment.setAttachableId(attachableId);

        attachmentRepository.save(attachment);
        return attachmentMapper.toDto(attachment);
    }
}
