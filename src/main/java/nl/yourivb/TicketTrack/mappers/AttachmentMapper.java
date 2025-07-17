package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.attachment.AttachmentDto;
import nl.yourivb.TicketTrack.models.Attachment;
import nl.yourivb.TicketTrack.services.EntityMappingService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EntityMappingService.class)
public interface AttachmentMapper {
    @Mapping(target = "uploadedById", source = "uploadedBy")
    AttachmentDto toDto(Attachment attachment);
}
