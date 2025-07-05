package nl.yourivb.TicketTrack.dtos.assignmentGroup;

import jakarta.validation.constraints.NotBlank;

public class AssignmentGroupInputDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
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
