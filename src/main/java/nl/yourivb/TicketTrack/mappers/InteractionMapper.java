package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.interaction.InteractionDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionInputDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionPatchDto;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.services.EntityMappingService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = EntityMappingService.class)

public interface InteractionMapper {
    @Mapping(target = "serviceOfferingId", source = "serviceOffering")
    @Mapping(target = "assignmentGroupId", source = "assignmentGroup")
    @Mapping(target = "openedById", source = "openedBy")
    @Mapping(target = "openedForId", source = "openedFor")
    @Mapping(target = "closedById", source = "closedBy")
    @Mapping(target = "incidentId", source = "incident")
    InteractionDto toDto(Interaction interaction);

    @Mapping(target = "serviceOffering", source = "serviceOfferingId")
    @Mapping(target = "assignmentGroup", source = "assignmentGroupId")
    @Mapping(target = "openedFor", source = "openedForId")
    @Mapping(target = "state", source = "state")
    Interaction toModel(InteractionInputDto dto);
    void updateInteractionFromDto(InteractionInputDto dto, @MappingTarget Interaction interaction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchInteractionFromDto(InteractionPatchDto dto, @MappingTarget Interaction interaction);
}
