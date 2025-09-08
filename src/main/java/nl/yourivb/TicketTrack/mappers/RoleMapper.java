package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.role.RoleDto;
import nl.yourivb.TicketTrack.dtos.role.RoleInputDto;
import nl.yourivb.TicketTrack.models.Role;
import nl.yourivb.TicketTrack.services.EntityMappingService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = EntityMappingService.class)
public interface RoleMapper {

    RoleDto toDto(Role role);

    Role toModel(RoleInputDto dto);

    void updateRoleFromDto(RoleInputDto dto, @MappingTarget Role role);
}