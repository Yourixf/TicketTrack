package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.InteractionDto;
import nl.yourivb.TicketTrack.dtos.InteractionInputDto;
import nl.yourivb.TicketTrack.dtos.InteractionPatchDto;
import nl.yourivb.TicketTrack.models.Interaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InteractionMapper {
    InteractionDto toDto(Interaction interaction);
    Interaction toModel(InteractionInputDto dto);
    void updateInteractionFromDto(InteractionInputDto dto, @MappingTarget Interaction interaction);
    void patchInteractionFromDto(InteractionPatchDto dto, @MappingTarget Interaction interaction);
}
