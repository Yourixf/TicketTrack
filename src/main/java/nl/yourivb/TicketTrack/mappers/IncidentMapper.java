package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.Incident.IncidentDto;
import nl.yourivb.TicketTrack.dtos.Incident.IncidentInputDto;
import nl.yourivb.TicketTrack.dtos.Incident.IncidentPatchDto;
import nl.yourivb.TicketTrack.models.Incident;
import nl.yourivb.TicketTrack.models.Interaction;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IncidentMapper {
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
