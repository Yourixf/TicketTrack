package nl.yourivb.TicketTrack.repositories;

import nl.yourivb.TicketTrack.models.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
}
