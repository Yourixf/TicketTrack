package nl.yourivb.TicketTrack.dtos.assignmentGroup;

import jakarta.persistence.Column;
import nl.yourivb.TicketTrack.models.AppUser;

import java.time.LocalDateTime;

public class AssignmentGroupDto {
    private Long id;
    private String name;
    private String email;

    @Column(updatable = false)
    private LocalDateTime created;
    private LocalDateTime lastModified;

    @Column(updatable = false)
    private AppUser createdBy;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public AppUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
    }
}
