package nl.yourivb.TicketTrack.dtos.attachment;

import org.springframework.core.io.Resource;

public class AttachmentDownloadDto {

    private Resource file;
    private String contentType;
    private String fileName;

    // this dto differs from other dtos, because this does not go to a user via json
    public AttachmentDownloadDto(Resource file, String contentType, String fileName) {
        this.file = file;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    public Resource getFile() {
        return file;
    }

    public void setFile(Resource file) {
        this.file = file;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
