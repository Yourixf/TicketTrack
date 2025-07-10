package nl.yourivb.TicketTrack.dtos.attachment;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import nl.yourivb.TicketTrack.models.AppUser;

import java.time.LocalDateTime;

public class AttachmentDto {
    private Long id;

    @Column(updatable = false)
    private String fileType;

    @Column(updatable = false)
    private String fileName;

    @Column(updatable = false)
    private LocalDateTime created;

    @Column(updatable = false)
    private String filePath;

    @Column(updatable = false)
    private String attachableType;

    @Column(updatable = false)
    private Long attachableId;

    @Column(updatable = false)
    private LocalDateTime lastModified;

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

    public void setCreated(LocalDateTime created) {
        this.created = created;
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
}
