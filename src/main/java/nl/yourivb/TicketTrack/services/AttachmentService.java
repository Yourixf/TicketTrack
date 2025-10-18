package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.attachment.AttachmentDownloadDto;
import nl.yourivb.TicketTrack.dtos.attachment.AttachmentDto;
import org.springframework.security.access.AccessDeniedException;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.FileStorageException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.AttachmentMapper;
import nl.yourivb.TicketTrack.models.*;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import nl.yourivb.TicketTrack.repositories.IncidentRepository;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import nl.yourivb.TicketTrack.security.SecurityUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final InteractionRepository interactionRepository;
    private final IncidentRepository incidentRepository;
    private final AppUserRepository appUserRepository;

    public AttachmentService(AttachmentRepository attachmentRepository,
                             AttachmentMapper attachmentMapper,
                             InteractionRepository interactionRepository,
                             IncidentRepository incidentRepository, AppUserRepository appUserRepository) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentMapper = attachmentMapper;
        this.interactionRepository = interactionRepository;
        this.incidentRepository = incidentRepository;
        this.appUserRepository = appUserRepository;
    }


    public void validateAttachableAndAccess(String attachableType, Long attachableId){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Boolean isAdmin = SecurityUtils.hasRole("ADMIN");
        Boolean isIT =  SecurityUtils.hasRole("IT");

        switch (attachableType) {
            case "Interaction" -> {
                Interaction interaction = interactionRepository.findById(attachableId).orElseThrow(() -> new RecordNotFoundException("Interaction " + attachableId  + " not found"));

                if (isAdmin || isIT) {
                    return;
                }

                Long openedById = interaction.getOpenedBy().getId();
                Long openedForId = interaction.getOpenedFor().getId();

                if (!Objects.equals(openedForId, currentUserId) && !Objects.equals(openedById, currentUserId)) {
                    throw new AccessDeniedException("You have no permission to access or alter this interaction");
                }
            }
            case "Incident" -> {
                Incident incident = incidentRepository.findById(attachableId).orElseThrow(() -> new RecordNotFoundException("Incident " + attachableId + " not found" ));

                if (isAdmin || isIT) {
                    return;
                }

                Long openedById = incident.getOpenedBy().getId();
                Long openedForId = incident.getOpenedFor().getId();

                if (!Objects.equals(openedForId, currentUserId) && !Objects.equals(openedById, currentUserId)) {
                    throw new AccessDeniedException("You have no permission to access or alter this incident");
                }

            }
            case "AppUser" -> {
                AppUser appUser = appUserRepository.findById(attachableId).orElseThrow(() -> new RecordNotFoundException("User " + attachableId + " not found"));

                if (isAdmin) {
                    return;
                }

                if (!Objects.equals(attachableId, currentUserId)) {
                    throw new AccessDeniedException("You have no permission to access or alter this user");
                }
            }
            default -> throw new BadRequestException("Unsupported parent type: " + attachableType);
        }
    }


    public List<AttachmentDto> getAllAttachments() {
//        return attachmentRepository.findAll().stream().map(attachmentMapper::toDto).toList();


        return attachmentRepository.findAll()
                .stream()
                .filter(attachment -> {
                    try {
                        validateAttachableAndAccess(attachment.getAttachableType(), attachment.getAttachableId());
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(attachmentMapper::toDto).toList();
    }

    public AttachmentDto getAttachmentById(Long id) {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Attachment " + id + " not found"));

        validateAttachableAndAccess(attachment.getAttachableType(), id);

        return attachmentMapper.toDto(attachment);
    }

    public AttachmentDto addAttachment(MultipartFile file, String attachableType, Long attachableId) {
        if (file.isEmpty()) {
            throw new BadRequestException("Empty file is not allowed");
        }

        validateAttachableAndAccess(attachableType, attachableId);

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        // to ensure unique fileName in uploads folder
        String uniqueFileName = UUID.randomUUID() + "_" + fileName;
        Path targetLocation = Paths.get("src/main/resources/uploads").resolve(uniqueFileName);

        try {
            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation);
        } catch (IOException e) {
            throw new FileStorageException("Error while saving file", e);
        }

        Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setStoredFileName(uniqueFileName);
        attachment.setAttachableType(attachableType);
        attachment.setAttachableId(attachableId);
        attachment.setFilePath(targetLocation.toString());
        attachment.setUploadedBy(SecurityUtils.getCurrentUserDetails().getAppUser());

        attachmentRepository.save(attachment);

        if (attachableType.equals("AppUser")) {
            // no orElse, switch case covers that.
            Optional<AppUser> user = appUserRepository.findById(attachableId);
            user.get().setProfilePicture(attachment);
            appUserRepository.save(user.get());
        }
        return attachmentMapper.toDto(attachment);
    }

    public void deleteAttachmentFromParent(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Attachment " + id + " not found"));

        validateAttachableAndAccess(attachment.getAttachableType(), id);

        Path path = Paths.get(attachment.getFilePath());
        String fileName = attachment.getFileName();

        if (attachment.getAttachableType().equals("AppUser")) {
            Optional<AppUser> user = appUserRepository.findById(attachment.getAttachableId());

            if (user.isPresent()) {
                user.get().setProfilePicture(null);
                appUserRepository.save(user.get());
            }
        }

        attachmentRepository.deleteById(id);

        List<Attachment> remainingAttachments = attachmentRepository.findByFileName(fileName);

        if (remainingAttachments.isEmpty()) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new FileStorageException("Failed to delete file from disk: " + path.getFileName(), e);
            }
        }
    }

    public List<AttachmentDto> getAllAttachmentsFromParent(String attachableType, Long attachableId) {
        validateAttachableAndAccess(attachableType, attachableId);
        return attachmentMapper.toDto(
                attachmentRepository.findByAttachableTypeAndAttachableId(attachableType, attachableId)
        );
    }

    public AttachmentDownloadDto downloadAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Attachment " + id + " not found"));

        Path filePath = Paths.get(attachment.getFilePath());
        String contentType;

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("File not found or not readable: " + filePath.getFileName());
            }

            contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return new AttachmentDownloadDto(resource, contentType, attachment.getFileName());
        } catch (IOException e) {
            throw new FileStorageException("Error while accessing file: " + filePath.getFileName(), e);
        }
    }

}
