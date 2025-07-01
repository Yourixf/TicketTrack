package nl.yourivb.TicketTrack.models;

import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int phoneNumber;
    private String email;
    private String info;

    @OneToOne
    @JoinColumn(name = "profile_picture_id")
    private Attachment profilePicture;

    private String password;
}
