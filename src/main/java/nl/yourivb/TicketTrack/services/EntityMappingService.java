package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.models.*;
import nl.yourivb.TicketTrack.repositories.*;
import org.springframework.stereotype.Service;

@Service
public class EntityMappingService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final AssignmentGroupRepository assignmentGroupRepository;
    private final AppUserRepository appUserRepository;
    private final InteractionRepository interactionRepository;
    private final IncidentRepository incidentRepository;

    public EntityMappingService(ServiceOfferingRepository serviceOfferingRepository,
                                AssignmentGroupRepository assignmentGroupRepository,
                                AppUserRepository appUserRepository, InteractionRepository interactionRepository, IncidentRepository incidentRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.assignmentGroupRepository = assignmentGroupRepository;
        this.appUserRepository = appUserRepository;
        this.interactionRepository = interactionRepository;
        this.incidentRepository = incidentRepository;
    }

    // for input DTO mapping (ID -> Object)
    public ServiceOffering getServiceOffering(Long id) {
        return serviceOfferingRepository.findById(id).orElse(null);
    }

    public AssignmentGroup getAssignmentGroup(Long id) {
        return assignmentGroupRepository.findById(id).orElse(null);
    }

    public AppUser getAppUser(Long id) {
        return appUserRepository.findById(id).orElse(null);
    }

    public Interaction getInteraction(Long id) { return interactionRepository.findById(id).orElse(null); }

    public Incident getIncident(Long id) { return incidentRepository.findById(id).orElse(null);}

    // for output DTO mapping (Object -> ID)
    public Long getServiceOfferingId(ServiceOffering serviceOffering) {
        return serviceOffering != null ? serviceOffering.getId() : null;
    }

    public Long getAssignmentGroupId(AssignmentGroup assignmentGroup) {
        return assignmentGroup != null ? assignmentGroup.getId() : null;
    }

    public Long getAppUserId(AppUser appUser) {
        return appUser != null ? appUser.getId() : null;
    }

    public Long getInteractionId(Interaction interaction) { return interaction != null ? interaction.getId() : null;}

    public Long getIncidentId(Incident incident) { return incident != null ? incident.getId() : null;}
}
