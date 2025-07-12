package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.models.AssignmentGroup;
import nl.yourivb.TicketTrack.models.ServiceOffering;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import nl.yourivb.TicketTrack.repositories.AssignmentGroupRepository;
import nl.yourivb.TicketTrack.repositories.ServiceOfferingRepository;
import org.springframework.stereotype.Service;

@Service
public class EntityMappingService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final AssignmentGroupRepository assignmentGroupRepository;
    private final AppUserRepository appUserRepository;

    public EntityMappingService(ServiceOfferingRepository serviceOfferingRepository,
                                AssignmentGroupRepository assignmentGroupRepository,
                                AppUserRepository appUserRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.assignmentGroupRepository = assignmentGroupRepository;
        this.appUserRepository = appUserRepository;
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
}
