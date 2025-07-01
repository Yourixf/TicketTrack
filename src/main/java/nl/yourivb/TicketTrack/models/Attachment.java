package nl.yourivb.TicketTrack.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Attachment {
    @Id
    @GeneratedValue
    private Long id;
    private String fileType;
    private String fileName;
    private LocalDateTime uploaded;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

    private String filePath;
}
