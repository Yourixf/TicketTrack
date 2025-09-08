package nl.yourivb.TicketTrack.dtos.note;

import jakarta.persistence.Column;
import nl.yourivb.TicketTrack.models.enums.NoteVisibility;

import java.time.LocalDateTime;

public class NoteDto {
    private Long id;

    //only updatable for admins
    private String content;

    @Column(updatable = false)
    private LocalDateTime created;

    @Column(updatable = false)
    private String noteableType;

    @Column(updatable = false)
    private Long noteableId;

    @Column(updatable = false)
    private NoteVisibility visibility;

    @Column(updatable = false)
    private Long createdById;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getNoteableType() {
        return noteableType;
    }

    public void setNoteableType(String noteableType) {
        this.noteableType = noteableType;
    }

    public Long getNoteableId() {
        return noteableId;
    }

    public void setNoteableId(Long noteableId) {
        this.noteableId = noteableId;
    }

    public NoteVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(NoteVisibility visibility) {
        this.visibility = visibility;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
}
