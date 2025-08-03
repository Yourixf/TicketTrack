package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.AppUser.AppUserDto;
import nl.yourivb.TicketTrack.dtos.AppUser.AppUserInputDto;
import nl.yourivb.TicketTrack.dtos.AppUser.AppUserPatchDto;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.mappers.AppUserMapper;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import org.springframework.stereotype.Service;

import static nl.yourivb.TicketTrack.utils.AppUtils.allFieldsNull;

import java.util.List;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    public AppUserService(AppUserRepository repo, AppUserMapper mapper) {
        this.appUserRepository = repo;
        this.appUserMapper = mapper;
    }

    public List<AppUserDto> getAllUsers() {
        return appUserRepository.findAll().stream()
                .map(appUserMapper::toDto)
                .toList();
    }

    public AppUserDto getUserById(Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("User " + id + " not found"));

        return appUserMapper.toDto(appUser);
    }

    public AppUserDto createUser(AppUserInputDto dto) {
        if (dto.getRoleId() == null) {
            dto.setRoleId(3L); // sets default role to customer
        }

        AppUser appUser = appUserMapper.toModel(dto);

        appUserRepository.save(appUser);
        return appUserMapper.toDto(appUser);
    }

    public AppUserDto updateUser(Long id, AppUserInputDto dto) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User " + id + " not found"));

        appUserMapper.updateAppUserFromDto(dto, appUser);
        appUserRepository.save(appUser);

        return appUserMapper.toDto(appUser);
    }

    public AppUserDto patchUser(Long id, AppUserPatchDto dto) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User " + id + " not found"));

        if (allFieldsNull(dto)) {
            throw new BadRequestException("No valid fields provided for patch");
        }

        appUserMapper.patchAppUserFromDto(dto, appUser);
        appUserRepository.save(appUser);

        return appUserMapper.toDto(appUser);
    }

    public void deleteUser(Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("User " + id + " not found"));

        appUserRepository.deleteById(id);
    }
}
