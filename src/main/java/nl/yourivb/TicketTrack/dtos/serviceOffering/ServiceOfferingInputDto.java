package nl.yourivb.TicketTrack.dtos.serviceOffering;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ServiceOfferingInputDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Default SLA in days is required")
    @Min(value = 0)
    private int defaultSlaInDays;

//    @ExcistInDatabase(repository = AssignmentGroupRepository.class, message = "Assignment group id not found in database")
    @NotNull(message = "Assignmentgroup is required")
    private Long assignmentGroupId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefaultSlaInDays() {
        return defaultSlaInDays;
    }

    public void setDefaultSlaInDays(int defaultSlaInDays) {
        this.defaultSlaInDays = defaultSlaInDays;
    }

    public Long getAssignmentGroupId() {
        return assignmentGroupId;
    }

    public void setAssignmentGroupId(Long assignmentGroupId) {
        this.assignmentGroupId = assignmentGroupId;
    }
}
