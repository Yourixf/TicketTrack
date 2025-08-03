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
    private final AttachmentRepository attachmentRepository;
    private final RoleRepository roleRepository;

    public EntityMappingService(ServiceOfferingRepository serviceOfferingRepository,
                                 AssignmentGroupRepository assignmentGroupRepository,
                                 AppUserRepository appUserRepository,
                                 InteractionRepository interactionRepository,
                                 IncidentRepository incidentRepository,
                                 AttachmentRepository attachmentRepository,
                                 RoleRepository roleRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.assignmentGroupRepository = assignmentGroupRepository;
        this.appUserRepository = appUserRepository;
        this.interactionRepository = interactionRepository;
        this.incidentRepository = incidentRepository;
        this.attachmentRepository = attachmentRepository;
        this.roleRepository = roleRepository;
    }

    // Input DTO mapping (ID -> Object)
    public ServiceOffering getServiceOffering(Long id) {
        if (id == null) return null;
        return serviceOfferingRepository.findById(id).orElse(null);
    }

    public AssignmentGroup getAssignmentGroup(Long id) {
        if (id == null) return null;
        return assignmentGroupRepository.findById(id).orElse(null);
    }

    public AppUser getAppUser(Long id) {
        if (id == null) return null;
        return appUserRepository.findById(id).orElse(null);
    }

    public Interaction getInteraction(Long id) {
        if (id == null) return null;
        return interactionRepository.findById(id).orElse(null);
    }

    public Incident getIncident(Long id) {
        if (id == null) return null;
        return incidentRepository.findById(id).orElse(null);
    }

    public Attachment getAttachment(Long id) {
        if (id == null) return null;
        return attachmentRepository.findById(id).orElse(null);
    }

    public Role getRole(Long id) {
        if (id == null) return null;
        return roleRepository.findById(id).orElse(null);
    }

    // Output DTO mapping (Object -> ID)
    public Long getServiceOfferingId(ServiceOffering serviceOffering) {
        return serviceOffering != null ? serviceOffering.getId() : null;
    }

    public Long getAssignmentGroupId(AssignmentGroup assignmentGroup) {
        return assignmentGroup != null ? assignmentGroup.getId() : null;
    }

    public Long getAppUserId(AppUser appUser) {
        return appUser != null ? appUser.getId() : null;
    }

    public Long getInteractionId(Interaction interaction) {
        return interaction != null ? interaction.getId() : null;
    }

    public Long getIncidentId(Incident incident) {
        return incident != null ? incident.getId() : null;
    }

    public Long getAttachmentId(Attachment attachment) {
        return attachment != null ? attachment.getId() : null;
    }

    public Long getRoleId(Role role) {
        return role != null ? role.getId() : null;
    }
}
