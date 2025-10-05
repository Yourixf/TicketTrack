package nl.yourivb.TicketTrack.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String storedFileName;
    private String filePath;
    private LocalDateTime created;

    private String attachableType;
    private Long attachableId;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private AppUser uploadedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime uploaded) {
        this.created = uploaded;
    }


    public String getAttachableType() {
        return attachableType;
    }

    public void setAttachableType(String attachableType) {
        this.attachableType = attachableType;
    }

    public Long getAttachableId() {
        return attachableId;
    }

    public void setAttachableId(Long attachableId) {
        this.attachableId = attachableId;
    }


    public AppUser getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(AppUser uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
    }
}
