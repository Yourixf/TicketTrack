package nl.yourivb.TicketTrack.models;

import jakarta.persistence.*;
import nl.yourivb.TicketTrack.models.enums.Category;
import nl.yourivb.TicketTrack.models.enums.Channel;
import nl.yourivb.TicketTrack.models.enums.InteractionState;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Interaction {

    @Id
    @GeneratedValue
    private Long id;

    private String number;
    private LocalDateTime created;
    private LocalDateTime closed;
    private String shortDescription;
    private String description;

    @ManyToOne
    @JoinColumn(name = "opened_by_id")
    private User openedBy;

    @ManyToOne
    @JoinColumn(name = "opened_for_id")
    private User openedFor;

    @ManyToOne
    @JoinColumn(name = "closed_by_id")
    private User closedBy;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private InteractionState state;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    private ServiceOffering serviceOffering;
    private AssignmentGroup assignmentGroup;

    @OneToMany
    @JoinColumn(name = "")
    private List<Attachment> attachments;


}
