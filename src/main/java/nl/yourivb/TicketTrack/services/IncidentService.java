package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.incident.IncidentDto;
import nl.yourivb.TicketTrack.dtos.incident.IncidentInputDto;
import nl.yourivb.TicketTrack.dtos.incident.IncidentPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.CustomException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.IncidentMapper;
import nl.yourivb.TicketTrack.models.Attachment;
import nl.yourivb.TicketTrack.models.Incident;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.models.enums.IncidentState;
import nl.yourivb.TicketTrack.models.enums.InteractionState;
import nl.yourivb.TicketTrack.models.enums.Priority;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import nl.yourivb.TicketTrack.repositories.IncidentRepository;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
import nl.yourivb.TicketTrack.security.SecurityUtils;
import nl.yourivb.TicketTrack.utils.AppUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static nl.yourivb.TicketTrack.utils.AppUtils.*;


@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final InteractionRepository interactionRepository;
    private final IncidentMapper incidentMapper;
    private final NoteRepository noteRepository;
    private final AttachmentRepository attachmentRepository;

    public IncidentService(IncidentRepository incidentRepository,
                           InteractionRepository interactionRepository,
                           IncidentMapper incidentMapper, NoteRepository noteRepository,
                           AttachmentRepository attachmentRepository) {
        this.incidentRepository = incidentRepository;
        this.interactionRepository = interactionRepository;
        this.incidentMapper = incidentMapper;
        this.noteRepository = noteRepository;
        this.attachmentRepository = attachmentRepository;
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

    // mutation free check
    private boolean isEligibleForAutoClose(Incident inc) {
        return inc.getState() == IncidentState.RESOLVED
                && inc.getResolved() != null
                && !inc.getResolved().plusDays(7).isAfter(LocalDateTime.now()); // >= 7 dagen
    }

    // closes incident if eligible.
    private void persistAutoCloseIfEligible(Incident incident) {
        if (isEligibleForAutoClose(incident)) {
            incident.setState(IncidentState.CLOSED);
            incident.setClosed(incident.getResolved().plusDays(7));
            incident.setClosedBy(incident.getResolvedBy());
            incidentRepository.save(incident);
        }
    }

    // for the getter so we don't change data on get request but still show business logic.
    private void applyEffectiveClosedToDtoIfNeeded(Incident incident, IncidentDto dto) {
        if (incident.getState() != IncidentState.CLOSED && isEligibleForAutoClose(incident)) {
            dto.setState(IncidentState.CLOSED);
            dto.setClosed(incident.getResolved().plusDays(7));

            if (incident.getResolvedBy() != null) {
                dto.setClosedById(incident.getResolvedBy().getId());
            } else {
                dto.setClosedById(null);
            }
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
            // valide on resolved reason
            if (incident.getResolvedReason() == null) {
                throw new BadRequestException("Resolved reason cannot be empty when state is on resolved.");
            }
            // first time setting state on resolved, set time
            if (incident.getResolved() == null) {
                incident.setResolved(LocalDateTime.now());
                incident.setResolvedBy(SecurityUtils.getCurrentUserDetails().getAppUser());
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

            // initial canceled
            if (incident.getCanceled() == null) {
                incident.setCanceled(LocalDateTime.now());
                incident.setCanceledBy(SecurityUtils.getCurrentUserDetails().getAppUser());
            }
            // no else if because reopening a canceled incident is not allowed.
        }
    }

    private void validateStateTransition(IncidentState previousState, Incident incident) {
        IncidentState newState = incident.getState();

        if (previousState == IncidentState.CLOSED) {
            throw new CustomException("Cannot edit closed incident", HttpStatus.CONFLICT);
        }

        if (previousState == IncidentState.CANCELED && newState != IncidentState.CANCELED) {
            throw new CustomException("Cannot reopen a canceled incident", HttpStatus.CONFLICT);
        }

        if (newState == IncidentState.CLOSED) {
            throw new CustomException("Cannot manually close incident", HttpStatus.CONFLICT);
        }
    }

    public List<IncidentDto> getAllIncidents() {
        return incidentRepository.findAll()
                .stream()
                .filter(incident -> {
                    try {
                        validateTicketAccess(incident.getOpenedBy().getId(), incident.getOpenedFor().getId());
                        return true;
                    } catch (Exception e){
                        return false;
                    }
                })
                .map(incident -> {
                    AppUtils.enrichWithRelations(
                            incident,
                            "Incident",
                            incident.getId(),
                            noteRepository,
                            attachmentRepository
                    );

                    IncidentDto dto = incidentMapper.toDto(incident);
                    applyEffectiveClosedToDtoIfNeeded(incident, dto);
                    return dto;

                })
                .toList();
    }

    public IncidentDto getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Incident " + id + " not found" ));
        validateTicketAccess(incident.getOpenedBy().getId(), incident.getOpenedFor().getId());
        AppUtils.enrichWithRelations(
                incident,
                "Incident",
                id,
                noteRepository,
                attachmentRepository
        );

        IncidentDto dto = incidentMapper.toDto(incident);
        applyEffectiveClosedToDtoIfNeeded(incident, dto);
        return dto;
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

        AppUtils.enrichWithRelations(
                incident,
                "Incident",
                incident.getId(),
                noteRepository,
                attachmentRepository
        );

        return incidentMapper.toDto(incident);
    }

    public IncidentDto updateIncident(Long id, IncidentInputDto newIncident) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Incident " + id + " not found"));

        persistAutoCloseIfEligible(incident);

        IncidentState prevState = incident.getState();

        incidentMapper.updateIncidentFromDto(newIncident, incident);

        validateStateTransition(prevState, incident);

        incident.setResolveBefore(
                calculateResolveBeforeDate(
                        incident.getCreated(),
                        calculateSlaInDays(incident.getPriority(), incident.getServiceOffering().getDefaultSlaInDays())
                ));

        validateResolvedState(incident);
        validateOnHoldState(incident);
        validateCanceledState(incident);


        incidentRepository.save(incident);

        AppUtils.enrichWithRelations(
                incident,
                "Incident",
                id,
                noteRepository,
                attachmentRepository
        );

        return incidentMapper.toDto(incident);
    }


    public IncidentDto patchIncident(Long id, IncidentPatchDto patchedIncident) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Incident " + id + " not found"));

        if (allFieldsNull(patchedIncident)) {
            throw new BadRequestException("No valid fields provided for patch");
        }

        persistAutoCloseIfEligible(incident);

        IncidentState prevState = incident.getState();
        incidentMapper.patchIncidentFromDto(patchedIncident, incident);
        validateStateTransition(prevState, incident);

        incident.setResolveBefore(
                calculateResolveBeforeDate(
                        incident.getCreated(),
                        calculateSlaInDays(incident.getPriority(), incident.getServiceOffering().getDefaultSlaInDays())
                ));

        validateResolvedState(incident);
        validateOnHoldState(incident);
        validateCanceledState(incident);

        incidentRepository.save(incident);

        AppUtils.enrichWithRelations(
                incident,
                "Incident",
                id,
                noteRepository,
                attachmentRepository
        );
        return incidentMapper.toDto(incident);
    }

    public void deleteIncident(Long id) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Incident " + id + " not found"));

        incidentRepository.deleteById(id);
    }

    public IncidentDto addChildInteractions(Long id, Set<Long> interactionIds) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Incident " + id + " not found"));

        List<Interaction> childInteractions = interactionIds.stream()
                .map(childId -> interactionRepository.findById(childId)
                        .orElseThrow(() -> new RecordNotFoundException("Interaction " + childId + " not found")))
                .collect(Collectors.toList());

        incidentRepository.save(incident);

        return incidentMapper.toDto(incident);
    }


}
