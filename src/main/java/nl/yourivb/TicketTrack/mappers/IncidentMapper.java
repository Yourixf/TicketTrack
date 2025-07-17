package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.Incident.IncidentDto;
import nl.yourivb.TicketTrack.dtos.Incident.IncidentInputDto;
import nl.yourivb.TicketTrack.dtos.Incident.IncidentPatchDto;
import nl.yourivb.TicketTrack.models.Incident;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.services.EntityMappingService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = EntityMappingService.class)
public interface IncidentMapper {
    @Mapping(target = "serviceOfferingId", source = "serviceOffering")
    @Mapping(target = "assignmentGroupId", source = "assignmentGroup")
    @Mapping(target = "openedById", source = "openedBy")
    @Mapping(target = "openedForId", source = "openedFor")
    @Mapping(target = "resolvedById", source = "resolvedBy")
    @Mapping(target = "closedById", source = "closedBy")
    @Mapping(target = "escalatedFromId", source = "escalatedFrom")
    IncidentDto toDto(Incident incident);

    Incident toModel(IncidentDto dto);

    void updateIncidentFromDto(IncidentInputDto dto, @MappingTarget Incident incident);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchIncidentFromDto(IncidentPatchDto dto, @MappingTarget Incident incident);

    @Mapping(source = "state", target = "state")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    Incident fromInteraction(Interaction interaction);
}
