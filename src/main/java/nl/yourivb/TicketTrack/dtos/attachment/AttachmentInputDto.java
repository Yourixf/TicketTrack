package nl.yourivb.TicketTrack.dtos.attachment;

import jakarta.persistence.Column;
import nl.yourivb.TicketTrack.models.AppUser;

import java.time.LocalDateTime;

public class AttachmentInputDto {
    private String fileType;
    private String fileName;


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
}
