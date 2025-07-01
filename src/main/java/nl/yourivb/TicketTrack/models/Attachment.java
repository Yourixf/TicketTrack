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
    private String filePath;
    private String attachableType;
    private Long attachableId;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

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

    public LocalDateTime getUploaded() {
        return uploaded;
    }

    public void setUploaded(LocalDateTime uploaded) {
        this.uploaded = uploaded;
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

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
}
