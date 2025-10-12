package nl.yourivb.TicketTrack.repositories;

import nl.yourivb.TicketTrack.models.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByAttachableTypeAndAttachableId(String attachableType, Long attachableId);
    List<Attachment> findByFileName(String fileName);
}
