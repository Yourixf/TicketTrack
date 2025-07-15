package nl.yourivb.TicketTrack.dtos.Incident;

import jakarta.persistence.*;
import nl.yourivb.TicketTrack.models.*;
import nl.yourivb.TicketTrack.models.enums.*;

import java.time.LocalDateTime;
import java.util.List;

public class IncidentDto {
    private Long id;
    private String number;
    private LocalDateTime created;
    private LocalDateTime closed;
    private LocalDateTime resolved;
    private LocalDateTime lastModified;
    private LocalDateTime onHoldSince;
    private String shortDescription;
    private String description;
    private Category category;
    private IncidentState state;
    private Channel channel;
    private Priority priority;
    private OnHoldReason onHoldReason;
    private ResolvedReason resolvedReason;
    private CanceledReason canceledReason;
    private ServiceOffering serviceOffering;
    private AssignmentGroup assignmentGroup;
    private AppUser openedBy;
    private AppUser openedFor;
    private AppUser resolvedBy;
    private AppUser closedBy;
    private Interaction escalatedFrom;
    private List<Interaction> childInteractions;
    private List<Attachment> attachments;
    private List<Note> notes;

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

    public ServiceOffering getServiceOffering() {
        return serviceOffering;
    }

    public void setServiceOffering(ServiceOffering serviceOffering) {
        this.serviceOffering = serviceOffering;
    }

    public AssignmentGroup getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(AssignmentGroup assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AppUser getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(AppUser openedBy) {
        this.openedBy = openedBy;
    }

    public AppUser getOpenedFor() {
        return openedFor;
    }

    public void setOpenedFor(AppUser openedFor) {
        this.openedFor = openedFor;
    }

    public AppUser getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(AppUser resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public AppUser getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(AppUser closedBy) {
        this.closedBy = closedBy;
    }

    public Interaction getEscalatedFrom() {
        return escalatedFrom;
    }

    public void setEscalatedFrom(Interaction escalatedFrom) {
        this.escalatedFrom = escalatedFrom;
    }

    public List<Interaction> getChildInteractions() {
        return childInteractions;
    }

    public void setChildInteractions(List<Interaction> childInteractions) {
        this.childInteractions = childInteractions;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
