package nl.yourivb.TicketTrack.dtos.assignmentGroup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AssignmentGroupInputDto {
    @NotBlank
    @Size(min = 2, max = 255)
    private String name;

    @Email
    @NotBlank
    @Size(min = 2, max = 255)
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
