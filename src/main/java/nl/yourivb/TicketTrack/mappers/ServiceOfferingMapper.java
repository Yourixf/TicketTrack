package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingDto;
import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingInputDto;
import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingPatchDto;
import nl.yourivb.TicketTrack.models.ServiceOffering;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ServiceOfferingMapper {
    ServiceOfferingDto toDto(ServiceOffering serviceOffering);
    ServiceOffering toModel(ServiceOfferingInputDto dto);
    void updateServiceOfferingFromDto(ServiceOfferingInputDto dto, @MappingTarget ServiceOffering serviceOffering);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchServiceOfferingFromDto(ServiceOfferingPatchDto dto, @MappingTarget ServiceOffering serviceOffering);
}
