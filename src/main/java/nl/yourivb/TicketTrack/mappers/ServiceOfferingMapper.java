package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.serviceoffering.ServiceOfferingDto;
import nl.yourivb.TicketTrack.dtos.serviceoffering.ServiceOfferingInputDto;
import nl.yourivb.TicketTrack.dtos.serviceoffering.ServiceOfferingPatchDto;
import nl.yourivb.TicketTrack.models.ServiceOffering;
import nl.yourivb.TicketTrack.services.EntityMappingService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = EntityMappingService.class)
public interface ServiceOfferingMapper {
    @Mapping(target = "assignmentGroupId", source = "assignmentGroup")
    @Mapping(target = "createdById", source = "createdBy")
    ServiceOfferingDto toDto(ServiceOffering serviceOffering);

    @Mapping(target = "assignmentGroup", source = "assignmentGroupId")
    ServiceOffering toModel(ServiceOfferingInputDto dto);
    void updateServiceOfferingFromDto(ServiceOfferingInputDto dto, @MappingTarget ServiceOffering serviceOffering);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchServiceOfferingFromDto(ServiceOfferingPatchDto dto, @MappingTarget ServiceOffering serviceOffering);
}
