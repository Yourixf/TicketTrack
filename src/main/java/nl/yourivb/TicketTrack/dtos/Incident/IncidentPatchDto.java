package nl.yourivb.TicketTrack.dtos.Incident;

import jakarta.validation.constraints.Size;
import nl.yourivb.TicketTrack.models.enums.*;

import java.util.List;

public class IncidentPatchDto {

    @Size(min = 2, max = 255)
    private String shortDescription;

    @Size(min = 2, max = 1500)
    private String description;

    private Category category;
    private IncidentState state;
    private Channel channel;
    private Priority priority;
    private OnHoldReason onHoldReason;
    private ResolvedReason resolvedReason;
    private CanceledReason canceledReason;
    private Long serviceOfferingId;
    private Long assignmentGroupId;
    private Long openedForId;
    private List<Long> childInteractionsId;

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

    public IncidentState getState() {
        return state;
    }

    public void setState(IncidentState state) {
        this.state = state;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public OnHoldReason getOnHoldReason() {
        return onHoldReason;
    }

    public void setOnHoldReason(OnHoldReason onHoldReason) {
        this.onHoldReason = onHoldReason;
    }

    public ResolvedReason getResolvedReason() {
        return resolvedReason;
    }

    public void setResolvedReason(ResolvedReason resolvedReason) {
        this.resolvedReason = resolvedReason;
    }

    public CanceledReason getCanceledReason() {
        return canceledReason;
    }

    public void setCanceledReason(CanceledReason canceledReason) {
        this.canceledReason = canceledReason;
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

    public List<Long> getChildInteractionsId() {
        return childInteractionsId;
    }

    public void setChildInteractionsId(List<Long> childInteractionsId) {
        this.childInteractionsId = childInteractionsId;
    }
}
