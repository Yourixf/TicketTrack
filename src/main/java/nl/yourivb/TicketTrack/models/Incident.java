package nl.yourivb.TicketTrack.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import nl.yourivb.TicketTrack.models.enums.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Incident {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String number;

    @Column(updatable = false)
    private LocalDateTime created;

    @Column(updatable = false)
    private LocalDateTime closed;

    private LocalDateTime resolved;
    private LocalDateTime lastModified;
    private LocalDateTime onHoldSince;
    private LocalDateTime resolveBefore;
    private LocalDateTime canceled;

    private String shortDescription;
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private IncidentState state;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private OnHoldReason onHoldReason;

    @Enumerated(EnumType.STRING)
    private ResolvedReason resolvedReason;

    @Enumerated(EnumType.STRING)
    private CanceledReason canceledReason;

    @ManyToOne
    @JoinColumn(name = "service_offering_id")
    private ServiceOffering serviceOffering;

    @ManyToOne
    @JoinColumn(name = "assignment_group_id")
    private AssignmentGroup assignmentGroup;

    @ManyToOne
    @JoinColumn(name = "opened_by_id")
    private AppUser openedBy;

    @ManyToOne
    @JoinColumn(name = "opened_for_id")
    private AppUser openedFor;

    @ManyToOne
    @JoinColumn(name = "resolved_by_id")
    private AppUser resolvedBy;

    @ManyToOne
    @JoinColumn(name = "closed_by_id")
    private AppUser closedBy;

    @ManyToOne
    @JoinColumn(name = "canceled_by_id")
    private AppUser canceledBy;

    @ManyToOne
    @JoinColumn(name = "escalated_from_id")
    private Interaction escalatedFrom;

    // This tag makes sure the underlying variable doesn't get saved into the database, but editable in service layer so you can easily export it via DTO
    @Transient
    private List<Attachment> attachments;

    @Transient
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

    public LocalDateTime getResolveBefore() {
        return resolveBefore;
    }

    public void setResolveBefore(LocalDateTime resolveBefore) {
        this.resolveBefore = resolveBefore;
    }

    public LocalDateTime getCanceled() {
        return canceled;
    }

    public void setCanceled(LocalDateTime canceled) {
        this.canceled = canceled;
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

    public AppUser getCanceledBy() {
        return canceledBy;
    }

    public void setCanceledBy(AppUser canceledBy) {
        this.canceledBy = canceledBy;
    }

    public Interaction getEscalatedFrom() {
        return escalatedFrom;
    }

    public void setEscalatedFrom(Interaction escalatedFrom) {
        this.escalatedFrom = escalatedFrom;
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

    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.priority = Priority.NORMAL;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModified = LocalDateTime.now();
    }
}
