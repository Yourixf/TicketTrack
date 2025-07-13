package nl.yourivb.TicketTrack.repositories;

import nl.yourivb.TicketTrack.models.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByAttachableTypeAndAttachableId(String noteableType, Long noteableId);
}
