package nl.yourivb.TicketTrack.repositories;

import nl.yourivb.TicketTrack.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
