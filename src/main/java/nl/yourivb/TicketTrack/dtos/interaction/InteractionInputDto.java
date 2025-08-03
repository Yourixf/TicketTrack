package nl.yourivb.TicketTrack.dtos.interaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nl.yourivb.TicketTrack.models.enums.Category;
import nl.yourivb.TicketTrack.models.enums.Channel;
import nl.yourivb.TicketTrack.models.enums.InteractionState;
import nl.yourivb.TicketTrack.repositories.AssignmentGroupRepository;
import nl.yourivb.TicketTrack.repositories.ServiceOfferingRepository;
import nl.yourivb.TicketTrack.validators.ExcistInDatabase;

public class InteractionInputDto {
    @NotBlank
    @Size(min = 2, max = 255)
    private String shortDescription;

    @NotBlank
    @Size(min = 2, max = 1500)
    private String description;

    @NotNull
    private Category category;

    private InteractionState state;

    @NotNull
    private Channel channel;

    @ExcistInDatabase(repository = ServiceOfferingRepository.class, message = "Service offering id not found in database")
    @NotNull
    private Long serviceOfferingId;

    @ExcistInDatabase(repository = AssignmentGroupRepository.class, message = "Assignment group id not found in database")
    @NotNull
    private Long assignmentGroupId;

    @ExcistInDatabase(repository = AppUserRepository.class, message = "Opened for id not found in database")
    @NotNull
    private Long openedForId;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public InteractionState getState() {
        return state;
    }

    public void setState(InteractionState state) {
        this.state = state;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Long getServiceOfferingId() {
        return serviceOfferingId;
    }

    public void setServiceOfferingId(Long serviceOfferingId) {
        this.serviceOfferingId = serviceOfferingId;
    }

    public Long getAssignmentGroupId() {
        return assignmentGroupId;
    }

    public void setAssignmentGroupId(Long assignmentGroupId) {
        this.assignmentGroupId = assignmentGroupId;
    }

    public Long getOpenedForId() {
        return openedForId;
    }

    public void setOpenedForId(Long openedForId) {
        this.openedForId = openedForId;
    }
}