package nl.yourivb.TicketTrack.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import nl.yourivb.TicketTrack.models.enums.Category;
import nl.yourivb.TicketTrack.models.enums.Channel;
import nl.yourivb.TicketTrack.models.enums.InteractionState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String number;

    @Column(updatable = false)
    private LocalDateTime created;

    @Column(updatable = false)
    private LocalDateTime closed;

    private LocalDateTime lastModified;

    private String shortDescription;
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private InteractionState state;

    @Enumerated(EnumType.STRING)
    private Channel channel;

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
    @JoinColumn(name = "closed_by_id")
    private AppUser closedBy;

    @ManyToOne
    @JoinColumn(name = "incident_id")
    @JsonBackReference
    private Incident incident;

    // This tag makes sure the underlying variable doesn't get saved into the database, but editable in service layer so you can easily export it via DTO
    @Transient
    private List<Attachment> attachments;

    @Transient
    private List<Note> notes = new ArrayList<>();


    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

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

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
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

    public AppUser getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(AppUser closedBy) {
        this.closedBy = closedBy;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
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

        if (this.state == null) {
            this.state = InteractionState.NEW;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModified = LocalDateTime.now();
    }
}
