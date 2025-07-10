package nl.yourivb.TicketTrack.models;

import jakarta.persistence.*;
import nl.yourivb.TicketTrack.models.enums.NoteVisibility;

import java.time.LocalDateTime;

@Entity
public class Note {
    @Id
    @GeneratedValue
    private Long id;
    private String content;

    private LocalDateTime created;

    private String noteableType;
    private Long noteableId;

    @Enumerated(EnumType.STRING)
    private NoteVisibility visibility;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private AppUser createdBy;

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

    public void setVisibility(NoteVisibility noteVisibility) {
        this.visibility = noteVisibility;
    }

    public AppUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
    }

    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
    }
}
