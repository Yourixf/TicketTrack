package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.attachment.AttachmentDto;
import nl.yourivb.TicketTrack.dtos.attachment.AttachmentInputDto;
import nl.yourivb.TicketTrack.models.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    AttachmentDto toDto(Attachment attachment);
    Attachment toModel(AttachmentInputDto dto);

    void updateAttachmentFromDto(AttachmentInputDto dto, @MappingTarget Attachment attachment);
}
