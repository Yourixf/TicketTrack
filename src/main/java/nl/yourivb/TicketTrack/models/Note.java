package nl.yourivb.TicketTrack.models;

import jakarta.persistence.*;
import nl.yourivb.TicketTrack.models.enums.NoteType;

import java.time.LocalDateTime;

@Entity
public class Note {
    @Id
    @GeneratedValue
    private Long Id;
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    private NoteType noteType;
    private String noteableType;
    private Long noteableId;
}
