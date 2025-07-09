package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingDto;
import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingInputDto;
import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingPatchDto;
import nl.yourivb.TicketTrack.models.ServiceOffering;
import nl.yourivb.TicketTrack.services.EntityMappingService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = EntityMappingService.class)
public interface ServiceOfferingMapper {
    ServiceOfferingDto toDto(ServiceOffering serviceOffering);


    @Mapping(target = "assignmentGroup", source = "assignmentGroupId")
    ServiceOffering toModel(ServiceOfferingInputDto dto);
    void updateServiceOfferingFromDto(ServiceOfferingInputDto dto, @MappingTarget ServiceOffering serviceOffering);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchServiceOfferingFromDto(ServiceOfferingPatchDto dto, @MappingTarget ServiceOffering serviceOffering);
}
