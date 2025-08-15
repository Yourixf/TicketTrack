package nl.yourivb.TicketTrack.repositories;

import nl.yourivb.TicketTrack.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);


    Optional<AppUser> findAllByEmail(String email);
}
