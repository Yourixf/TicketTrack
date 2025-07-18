package nl.yourivb.TicketTrack.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "assignment_group")
public class AssignmentGroup {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private LocalDateTime created;
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
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

    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModified = LocalDateTime.now();
    }
}
