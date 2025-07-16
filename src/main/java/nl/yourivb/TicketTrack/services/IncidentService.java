package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.Incident.IncidentDto;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.IncidentMapper;
import nl.yourivb.TicketTrack.models.Attachment;
import nl.yourivb.TicketTrack.models.Incident;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.models.enums.InteractionState;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import nl.yourivb.TicketTrack.repositories.IncidentRepository;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
import org.springframework.stereotype.Service;

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

    public IncidentService(IncidentRepository incidentRepository,
                           InteractionRepository interactionRepository,
                           IncidentMapper incidentMapper, NoteRepository noteRepository, AttachmentRepository attachmentRepository) {
        this.incidentRepository = incidentRepository;
        this.interactionRepository = interactionRepository;
        this.incidentMapper = incidentMapper;
        this.noteRepository = noteRepository;
        this.attachmentRepository = attachmentRepository;
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
            copy.setAttachableId(incident.getId());
            copiedAttachments.add(copy);
        }
        attachmentRepository.saveAll(copiedAttachments);
        incident.setAttachments(copiedAttachments);
        incident.setEscalatedFrom(interaction);

        incidentRepository.save(incident);

        interaction.setIncident(incident);
        interaction.setState(InteractionState.CLOSED);
        interactionRepository.save(interaction);

        return incidentMapper.toDto(incident);
    }
}
