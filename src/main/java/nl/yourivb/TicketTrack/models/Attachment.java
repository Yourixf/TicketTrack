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

    @Column(updatable = false)
    private LocalDateTime created;

    private String filePath;
    private String attachableType;
    private Long attachableId;
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private AppUser uploadedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime uploaded) {
        this.created = uploaded;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
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
        this.lastModified = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModified = LocalDateTime.now();
    }
}
