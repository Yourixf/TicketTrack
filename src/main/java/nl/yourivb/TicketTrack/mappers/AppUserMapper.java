package nl.yourivb.TicketTrack.mappers;

import nl.yourivb.TicketTrack.dtos.appuser.AppUserDto;
import nl.yourivb.TicketTrack.dtos.appuser.AppUserInputDto;
import nl.yourivb.TicketTrack.dtos.appuser.AppUserPatchDto;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.services.EntityMappingService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = EntityMappingService.class)
public interface AppUserMapper {

    @Mapping(target = "profilePictureId", source = "profilePicture")
    @Mapping(target = "roleId", source = "role")
    AppUserDto toDto(AppUser user);

    @Mapping(target = "profilePicture", source = "profilePictureId")
    @Mapping(target = "role", source = "roleId")
    AppUser toModel(AppUserInputDto dto);

    @Mapping(target = "profilePicture", source = "profilePictureId")
    @Mapping(target = "role", source = "roleId")
    void updateAppUserFromDto(AppUserInputDto dto, @MappingTarget AppUser appUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchAppUserFromDto(AppUserPatchDto dto, @MappingTarget AppUser appUser);

}
