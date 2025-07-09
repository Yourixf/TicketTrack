package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.interaction.InteractionDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionInputDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionPatchDto;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.services.EntityMappingService;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = EntityMappingService.class)
public interface InteractionMapper {
    InteractionDto toDto(Interaction interaction);

    @Mapping(target = "serviceOffering", source = "serviceOfferingId")
    @Mapping(target = "assignmentGroup", source = "assignmentGroupId")
    @Mapping(target = "openedFor", source = "openedForId")
    Interaction toModel(InteractionInputDto dto);
    void updateInteractionFromDto(InteractionInputDto dto, @MappingTarget Interaction interaction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchInteractionFromDto(InteractionPatchDto dto, @MappingTarget Interaction interaction);
}
