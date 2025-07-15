package nl.yourivb.TicketTrack.dtos.interaction;

import nl.yourivb.TicketTrack.dtos.Note.NoteDto;
import nl.yourivb.TicketTrack.dtos.assignmentGroup.AssignmentGroupDto;
import nl.yourivb.TicketTrack.dtos.attachment.AttachmentDto;
import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingDto;
import nl.yourivb.TicketTrack.models.*;
import nl.yourivb.TicketTrack.models.enums.Category;
import nl.yourivb.TicketTrack.models.enums.Channel;
import nl.yourivb.TicketTrack.models.enums.InteractionState;

import java.time.LocalDateTime;
import java.util.List;


public class InteractionDto {
    private Long id;
    private String number;
    private LocalDateTime created;
    private LocalDateTime closed;
    private LocalDateTime lastModified;
    private String shortDescription;
    private String description;
    private Category category;
    private InteractionState state;
    private Channel channel;
    private ServiceOfferingDto serviceOffering;
    private AssignmentGroupDto assignmentGroup;
    private AppUser openedBy; // TODO when finsihed with appuser, replace with dto instead of entity
    private AppUser openedFor; // TODO APPUSERDTO
    private AppUser closedBy;
    private Incident incident; // TODO when finsihed with incident, replace with dto instead of entity
    private List<AttachmentDto> attachments;
    private List<NoteDto> notes;

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

    public ServiceOfferingDto getServiceOffering() {
        return serviceOffering;
    }

    public void setServiceOffering(ServiceOfferingDto serviceOffering) {
        this.serviceOffering = serviceOffering;
    }

    public AssignmentGroupDto getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(AssignmentGroupDto assignmentGroup) {
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

    public List<AttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDto> attachments) {
        this.attachments = attachments;
    }

    public List<NoteDto> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteDto> notes) {
        this.notes = notes;
    }
}
