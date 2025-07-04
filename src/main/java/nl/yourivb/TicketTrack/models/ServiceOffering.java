package nl.yourivb.TicketTrack.models;

import jakarta.persistence.*;

@Entity
@Table(name = "service_offering")
public class ServiceOffering {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int defaultSlaInDays;

    @ManyToOne
    @JoinColumn(name = "assignment_group_id")
    private AssignmentGroup assignmentGroup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public AssignmentGroup getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(AssignmentGroup assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }
}
