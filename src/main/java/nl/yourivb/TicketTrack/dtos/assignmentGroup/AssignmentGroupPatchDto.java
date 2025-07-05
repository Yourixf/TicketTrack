package nl.yourivb.TicketTrack.dtos.assignmentGroup;


import nl.yourivb.TicketTrack.validators.NotBlankIfPresent;

public class AssignmentGroupPatchDto {
    @NotBlankIfPresent(message = "Name can't be empty")
    private String name;

    @NotBlankIfPresent(message = "Email can't not be empty")
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
