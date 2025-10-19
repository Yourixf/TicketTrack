package nl.yourivb.TicketTrack.repositories;

import nl.yourivb.TicketTrack.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByNoteableTypeAndNoteableId(String noteableType, Long noteableId);
}
