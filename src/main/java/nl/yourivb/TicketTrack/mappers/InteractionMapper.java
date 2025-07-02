package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.InteractionDto;
import nl.yourivb.TicketTrack.dtos.InteractionInputDto;
import nl.yourivb.TicketTrack.models.Interaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InteractionMapper {
    InteractionDto toDto(Interaction interaction);
    Interaction toModel(InteractionInputDto dto);
}
