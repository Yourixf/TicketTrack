package nl.yourivb.TicketTrack.models;

import jakarta.persistence.*;
import nl.yourivb.TicketTrack.models.enums.NoteType;

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
    private NoteType noteType;

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

    public NoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(NoteType noteType) {
        this.noteType = noteType;
    }
}
