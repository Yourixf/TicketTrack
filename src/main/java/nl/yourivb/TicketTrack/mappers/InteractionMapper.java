package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.interaction.InteractionDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionInputDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionPatchDto;
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
