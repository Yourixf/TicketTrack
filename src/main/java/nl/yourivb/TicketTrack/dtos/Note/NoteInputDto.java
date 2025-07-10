package nl.yourivb.TicketTrack.dtos.Note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class NoteInputDto {
    @NotBlank
    @Size(min = 2, max = 1500)
    private String content;

    @NotBlank
    private String visibility;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
