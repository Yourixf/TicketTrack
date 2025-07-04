package nl.yourivb.TicketTrack.dtos.serviceOffering;

import nl.yourivb.TicketTrack.models.AssignmentGroup;

public class ServiceOfferingPatchDto {
    private String name;
    private int defaultSlaInDays;
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
