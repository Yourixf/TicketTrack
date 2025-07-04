package nl.yourivb.TicketTrack.repositories;

import nl.yourivb.TicketTrack.models.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
}
