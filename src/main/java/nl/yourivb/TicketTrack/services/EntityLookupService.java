package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.models.*;
import nl.yourivb.TicketTrack.repositories.*;
import org.springframework.stereotype.Service;

@Service
public class EntityLookupService {

    private final IncidentRepository incidentRepository;
    private final InteractionRepository interactionRepository;
    private final AssignmentGroupRepository assignmentGroupRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final AppUserRepository appUserRepository;

    public EntityLookupService(
            InteractionRepository interactionRepository,
            IncidentRepository incidentRepository,
            AssignmentGroupRepository assignmentGroupRepository,
            ServiceOfferingRepository serviceOfferingRepository,
            AppUserRepository appUserRepository
    ) {
        this.interactionRepository = interactionRepository;
        this.incidentRepository = incidentRepository;
        this.assignmentGroupRepository = assignmentGroupRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.appUserRepository = appUserRepository;
    }

    public Interaction getInteractionById(Long id) {
        return interactionRepository.findById(id).orElseThrow(() ->  new  RecordNotFoundException("Interaction " + id + " not found in the database"));
    }

    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id).orElseThrow(() ->  new  RecordNotFoundException("Incident " + id + " not found in the database"));
    }

    public AssignmentGroup getAssignmentGroupById(Long id) {
        return assignmentGroupRepository.findById(id).orElseThrow(() ->  new  RecordNotFoundException("AssignmentGroup " + id + " not found in the database"));
    }

    public ServiceOffering getServiceOfferingById(Long id) {
        return serviceOfferingRepository.findById(id).orElseThrow(() ->  new  RecordNotFoundException("ServiceOffering " + id + " not found in the database"));
    }

    public AppUser getAppUserById(Long id) {
        return appUserRepository.findById(id).orElseThrow(() ->  new  RecordNotFoundException("AppUser " + id + " not found in the database"));
    }
}
