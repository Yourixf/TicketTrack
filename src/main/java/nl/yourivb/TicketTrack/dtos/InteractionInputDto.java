package nl.yourivb.TicketTrack.dtos;

import jakarta.validation.constraints.NotNull;
import nl.yourivb.TicketTrack.models.*;
import nl.yourivb.TicketTrack.models.enums.Category;
import nl.yourivb.TicketTrack.models.enums.Channel;
import nl.yourivb.TicketTrack.models.enums.InteractionState;

import java.time.LocalDateTime;
import java.util.List;

public class InteractionInputDto {

    @NotNull(message = "Short description is required")
    private String shortDescription;
    @NotNull(message = "Description is required")
    private String description;
    @NotNull(message = "Category is required")
    private Category category;
    @NotNull(message = "Channel is required")
    private Channel channel;
    @NotNull(message = "Service offering is required")
    private Long serviceOfferingId;
    @NotNull(message = "Assignmentgroup is required")
    private Long assignmentGroupId;
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