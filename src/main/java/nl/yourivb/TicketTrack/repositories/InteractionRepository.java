package nl.yourivb.TicketTrack.repositories;

import nl.yourivb.TicketTrack.models.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {
}
