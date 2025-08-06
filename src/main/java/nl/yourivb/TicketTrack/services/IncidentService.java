package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.Incident.IncidentDto;
import nl.yourivb.TicketTrack.dtos.Incident.IncidentInputDto;
import nl.yourivb.TicketTrack.dtos.Incident.IncidentPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.CustomException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.IncidentMapper;
import nl.yourivb.TicketTrack.models.Attachment;
import nl.yourivb.TicketTrack.models.Incident;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.models.Note;
import nl.yourivb.TicketTrack.models.enums.IncidentState;
import nl.yourivb.TicketTrack.models.enums.InteractionState;
import nl.yourivb.TicketTrack.models.enums.Priority;
import nl.yourivb.TicketTrack.repositories.*;
import nl.yourivb.TicketTrack.security.SecurityUtils;
import nl.yourivb.TicketTrack.utils.AppUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static nl.yourivb.TicketTrack.utils.AppUtils.allFieldsNull;
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
                           IncidentMapper incidentMapper, NoteRepository noteRepository,
                           AttachmentRepository attachmentRepository,
                           ServiceOfferingRepository serviceOfferingRepository) {
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

    // TODO fix closed validator

    // for the get requests. Boolean return type for validateClosedState()
    private boolean checkIfClosedState(Incident incident){
        if (incident.getState() == IncidentState.RESOLVED && incident.getResolved() != null) {
            LocalDateTime resolvedDate = incident.getResolved();
            LocalDateTime currentDate = LocalDateTime.now();
            if (resolvedDate.plusDays(7).isBefore(currentDate)) {
                incident.setState(IncidentState.CLOSED);
                incident.setClosed(resolvedDate.plusDays(7));
                incidentRepository.save(incident);

                return true;
            }
        } else if (incident.getState() == IncidentState.CLOSED) {
            return true;
        }
        return false;
    }

    // for the patch & put requests.
    private void validateClosedState(Incident incident) {
        if (checkIfClosedState(incident)) {
                // if incident is closed and thus not editable
                throw new CustomException("Cannot edit closed incident", HttpStatus.CONFLICT);
        }
    }

    private void validateOnHoldState(Incident incident) {
        // if on hold proceed with first time check
        if (incident.getState() == IncidentState.ON_HOLD) {
            // first time setting state on hold, set time
            if (incident.getOnHoldSince() == null) {
                incident.setOnHoldSince(LocalDateTime.now());
            }
            // valide on hold reason
            if (incident.getOnHoldReason() == null) {
                throw new BadRequestException("On hold reason cannot be empty when state is on hold.");
            }
          // resets on hold since date if the incident is no longer on that state.
        } else if (incident.getOnHoldSince() != null) {
            incident.setOnHoldSince(null);
        }
    }

    private void validateResolvedState(Incident incident) {
        // if resolved proceed with first time check
        if (incident.getState() == IncidentState.RESOLVED) {
            // first time setting state on resolved, set time
            if (incident.getResolved() == null) {
                incident.setResolved(LocalDateTime.now());
                incident.setResolvedBy(SecurityUtils.getCurrentUserDetails().getAppUser());
            }
            // valide on resolved reason
            if (incident.getResolvedReason() == null) {
                throw new BadRequestException("Resolved reason cannot be empty when state is on resolved.");
            }
            // resets on resolved since date if the incident is no longer on that state.
        } else if (incident.getResolved() != null) {
            incident.setResolved(null);
            if (incident.getResolvedBy() != null) {
                incident.setResolvedBy(null);
            }
        }
    }

    private void validateCanceledState(Incident incident) {
        if (incident.getState() == IncidentState.CANCELED) {
            if (incident.getCanceledReason() == null) {
                throw new BadRequestException("Canceled reason cannot be empty when state is on canceled.");

            }
        }
    }

    public List<IncidentDto> getAllIncidents() {
        return incidentRepository.findAll()
                .stream()
                .map(incident -> {
                    checkIfClosedState(incident);
                    IncidentDto dto = incidentMapper.toDto(incident);

                    List<Note> notes = noteRepository.findByNoteableTypeAndNoteableId("Incident", incident.getId());
                    List<Long> noteIds = AppUtils.extractIds(notes, Note::getId);

                    List<Attachment> attachments = attachmentRepository.findByAttachableTypeAndAttachableId("Incident", incident.getId());
                    List<Long> attachmentIds = AppUtils.extractIds(attachments, Attachment::getId);

                    dto.setNoteIds(noteIds);
                    dto.setAttachmentIds(attachmentIds);

                    return dto;
                })
                .toList();
    }

    public IncidentDto getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Incident " + id + " not found" ));
        checkIfClosedState(incident);
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

        incidentRepository.save(incident);

        for (Attachment original : oldAttachments) {
            Attachment copy = new Attachment();
            copy.setFileName(original.getFileName());
            copy.setFilePath(original.getFilePath());
            copy.setStoredFileName(UUID.randomUUID() + "_" + original.getFileName());
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

        incident.setOpenedBy(SecurityUtils.getCurrentUserDetails().getAppUser());
        incidentRepository.save(incident);

        interaction.setIncident(incident);
        interaction.setState(InteractionState.CLOSED);
        interaction.setClosedBy(SecurityUtils.getCurrentUserDetails().getAppUser());
        interaction.setClosed(LocalDateTime.now());
        interactionRepository.save(interaction);

        return incidentMapper.toDto(incident);
    }

    public IncidentDto updateIncident(Long id, IncidentInputDto newIncident) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Incident " + id + " not found"));

        validateClosedState(incident);
        validateOnHoldState(incident);
        validateResolvedState(incident);
        validateCanceledState(incident);

        incidentMapper.updateIncidentFromDto(newIncident, incident);
        incident.setResolveBefore(
                calculateResolveBeforeDate(
                        incident.getCreated(),
                        calculateSlaInDays(incident.getPriority(), incident.getServiceOffering().getDefaultSlaInDays())
                ));


        incidentRepository.save(incident);

        IncidentDto incidentDto = incidentMapper.toDto(incident);

        List<Note> notes = noteRepository.findByNoteableTypeAndNoteableId("Incident", incident.getId());
        List<Long> noteIds = AppUtils.extractIds(notes, Note::getId);

        List<Attachment> attachments = attachmentRepository.findByAttachableTypeAndAttachableId("Incident", incident.getId());
        List<Long> attachmentIds = AppUtils.extractIds(attachments, Attachment::getId);

        incidentDto.setNoteIds(noteIds);
        incidentDto.setAttachmentIds(attachmentIds);

        return incidentDto;
    }


    public IncidentDto patchIncident(Long id, IncidentPatchDto patchedIncident) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Incident " + id + " not found"));

        validateClosedState(incident);
        validateOnHoldState(incident);
        validateResolvedState(incident);
        validateCanceledState(incident);

        if (allFieldsNull(patchedIncident)) {
            throw new BadRequestException("No valid fields provided for patch");
        }

        incidentMapper.patchIncidentFromDto(patchedIncident, incident);
        incident.setResolveBefore(
                calculateResolveBeforeDate(
                        incident.getCreated(),
                        calculateSlaInDays(incident.getPriority(), incident.getServiceOffering().getDefaultSlaInDays())
                ));
        incidentRepository.save(incident);

        IncidentDto incidentDto = incidentMapper.toDto(incident);

        List<Note> notes = noteRepository.findByNoteableTypeAndNoteableId("Incident", incident.getId());
        List<Long> noteIds = AppUtils.extractIds(notes, Note::getId);

        List<Attachment> attachments = attachmentRepository.findByAttachableTypeAndAttachableId("Incident", incident.getId());
        List<Long> attachmentIds = AppUtils.extractIds(attachments, Attachment::getId);

        incidentDto.setNoteIds(noteIds);
        incidentDto.setAttachmentIds(attachmentIds);

        return incidentDto;
    }

    public void deleteIncident(Long id) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Incident " + id + " not found"));

        incidentRepository.deleteById(id);
    }

}
