package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.Incident.IncidentDto;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.IncidentMapper;
import nl.yourivb.TicketTrack.models.Attachment;
import nl.yourivb.TicketTrack.models.Incident;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.models.Note;
import nl.yourivb.TicketTrack.models.enums.InteractionState;
import nl.yourivb.TicketTrack.models.enums.Priority;
import nl.yourivb.TicketTrack.repositories.*;
import nl.yourivb.TicketTrack.utils.AppUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static nl.yourivb.TicketTrack.utils.AppUtils.generateRegistrationNumber;


@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final InteractionRepository interactionRepository;
    private final IncidentMapper incidentMapper;
    private final NoteRepository noteRepository;
    private final AttachmentRepository attachmentRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;

    public IncidentService(IncidentRepository incidentRepository,
                           InteractionRepository interactionRepository,
                           IncidentMapper incidentMapper, NoteRepository noteRepository, AttachmentRepository attachmentRepository, ServiceOfferingRepository serviceOfferingRepository) {
        this.incidentRepository = incidentRepository;
        this.interactionRepository = interactionRepository;
        this.incidentMapper = incidentMapper;
        this.noteRepository = noteRepository;
        this.attachmentRepository = attachmentRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
    }

    private int calculateSlaInDays(Priority priority, int defaultSlaInDays) {
        return switch (priority) {
            case LOW -> defaultSlaInDays + 3;
            case NORMAL -> defaultSlaInDays;
            case HIGH -> Math.max(defaultSlaInDays - 2, 2); // ensures SLA is not lower than 2 for HIGH
            case CRITICAL -> 1;
        };
    }

    private LocalDateTime calculateResolveBeforeDate(LocalDateTime created, int slaInDays) {
        return created.plusDays(slaInDays);
    }

    public List<IncidentDto> getAllIncidents() {
        return incidentRepository.findAll()
                .stream()
                .map(incident -> {
                    incident.setNotes(noteRepository.findByNoteableTypeAndNoteableId("Incident", incident.getId()));
                    incident.setAttachments(attachmentRepository.findByAttachableTypeAndAttachableId("Incident", incident.getId()));
                return incidentMapper.toDto(incident);
                })
                .toList();
    }

    public IncidentDto getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Incident " + id + " not found" ));
        IncidentDto incidentDto = incidentMapper.toDto(incident);


        List<Note> notes = noteRepository.findByNoteableTypeAndNoteableId("Incident", incident.getId());
        List<Long> noteIds = AppUtils.extractIds(notes, Note::getId);

        List<Attachment> attachments = attachmentRepository.findByAttachableTypeAndAttachableId("Incident", incident.getId());
        List<Long> attachmentIds = AppUtils.extractIds(attachments, Attachment::getId);

        incidentDto.setNoteIds(noteIds);
        incidentDto.setAttachmentIds(attachmentIds);

        return incidentDto;
    }

    public IncidentDto escalateFromInteraction (Long interactionId) {
        Interaction interaction = interactionRepository.findById(interactionId).orElseThrow(() -> new RecordNotFoundException("Interaction " + interactionId + " not found"));

        Incident incident = incidentMapper.fromInteraction(interaction);
        incident.setNumber(generateRegistrationNumber("INC", incidentRepository));

        List<Attachment> oldAttachments = attachmentRepository.findByAttachableTypeAndAttachableId("Interaction", interaction.getId());
        List<Attachment> copiedAttachments = new ArrayList<>();

        for (Attachment original : oldAttachments) {
            Attachment copy = new Attachment();
            copy.setFileName(original.getFileName());
            copy.setFilePath(original.getFilePath());
            copy.setAttachableId(incident.getId());
            copy.setAttachableType("Incident");
            copiedAttachments.add(copy);
        }
        attachmentRepository.saveAll(copiedAttachments);
        incident.setAttachments(copiedAttachments);
        incident.setEscalatedFrom(interaction);
        incident.setPriority(Priority.NORMAL);
        incident.setResolveBefore(
                calculateResolveBeforeDate(
                    incident.getCreated(),
                    calculateSlaInDays(incident.getPriority(), incident.getServiceOffering().getDefaultSlaInDays())
                ));

        incidentRepository.save(incident);

        interaction.setIncident(incident);
        interaction.setState(InteractionState.CLOSED);
        interaction.setClosed(LocalDateTime.now());
        interactionRepository.save(interaction);

        return incidentMapper.toDto(incident);
    }
}
