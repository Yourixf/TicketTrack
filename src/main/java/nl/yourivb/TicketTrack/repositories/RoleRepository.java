package nl.yourivb.TicketTrack.repositories;

import nl.yourivb.TicketTrack.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
