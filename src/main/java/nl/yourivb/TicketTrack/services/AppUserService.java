package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.AppUser.AppUserDto;
import nl.yourivb.TicketTrack.dtos.AppUser.AppUserInputDto;
import nl.yourivb.TicketTrack.dtos.AppUser.AppUserPatchDto;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.AppUserMapper;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import org.springframework.stereotype.Service;

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
        return appUserMapper.toDto(
                appUserRepository.findById(id)
                        .orElseThrow(() -> new RecordNotFoundException("User not found"))
        );
    }

    public AppUserDto createUser(AppUserInputDto dto) {
        AppUser appUser = appUserMapper.toModel(dto);
        return appUserMapper.toDto(appUserRepository.save(appUser));
    }

    public AppUserDto updateUser(Long id, AppUserInputDto dto) {
        AppUser existing = appUserRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));

        AppUser updated = appUserMapper.toModel(dto);
        updated.setId(id);
        updated.setCreated(existing.getCreated());
        return appUserMapper.toDto(appUserRepository.save(updated));
    }

    public AppUserDto patchUser(Long id, AppUserPatchDto dto) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));

        appUserMapper.patchAppUserFromDto(dto, user);
        return appUserMapper.toDto(appUserRepository.save(user));
    }

    public void deleteUser(Long id) {
        if (!appUserRepository.existsById(id)) {
            throw new RecordNotFoundException("User not found");
        }
        appUserRepository.deleteById(id);
    }
}
