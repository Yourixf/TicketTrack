package nl.yourivb.TicketTrack.dtos.incident;

import nl.yourivb.TicketTrack.models.enums.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IncidentDto {
    private Long id;
    private String number;
    private LocalDateTime created;
    private LocalDateTime closed;
    private LocalDateTime resolved;
    private LocalDateTime canceled;
    private LocalDateTime lastModified;
    private LocalDateTime onHoldSince;
    private LocalDateTime resolveBefore;
    private String shortDescription;
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
    private Long openedById;
    private Long openedForId;
    private Long resolvedById;
    private Long closedById;
    private Long canceledById;
    private Long escalatedFromId;
    private List<Long> attachmentIds = new ArrayList<>();
    private List<Long> noteIds = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getClosed() {
        return closed;
    }

    public void setClosed(LocalDateTime closed) {
        this.closed = closed;
    }

    public LocalDateTime getResolved() {
        return resolved;
    }

    public void setResolved(LocalDateTime resolved) {
        this.resolved = resolved;
    }

    public LocalDateTime getCanceled() {
        return canceled;
    }

    public void setCanceled(LocalDateTime canceled) {
        this.canceled = canceled;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDateTime getOnHoldSince() {
        return onHoldSince;
    }

    public void setOnHoldSince(LocalDateTime onHoldSince) {
        this.onHoldSince = onHoldSince;
    }

    public LocalDateTime getResolveBefore() {
        return resolveBefore;
    }

    public void setResolveBefore(LocalDateTime resolveBefore) {
        this.resolveBefore = resolveBefore;
    }

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

    public Long getOpenedById() {
        return openedById;
    }

    public void setOpenedById(Long openedById) {
        this.openedById = openedById;
    }

    public Long getOpenedForId() {
        return openedForId;
    }

    public void setOpenedForId(Long openedForId) {
        this.openedForId = openedForId;
    }

    public Long getResolvedById() {
        return resolvedById;
    }

    public void setResolvedById(Long resolvedById) {
        this.resolvedById = resolvedById;
    }

    public Long getClosedById() {
        return closedById;
    }

    public void setClosedById(Long closedById) {
        this.closedById = closedById;
    }

    public Long getCanceledById() {
        return canceledById;
    }

    public void setCanceledById(Long canceledById) {
        this.canceledById = canceledById;
    }

    public Long getEscalatedFromId() {
        return escalatedFromId;
    }

    public void setEscalatedFromId(Long escalatedFromId) {
        this.escalatedFromId = escalatedFromId;
    }

    public List<Long> getAttachmentIds() {
        return attachmentIds;
    }

    public void setAttachmentIds(List<Long> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    public List<Long> getNoteIds() {
        return noteIds;
    }

    public void setNoteIds(List<Long> noteIds) {
        this.noteIds = noteIds;
    }
}
