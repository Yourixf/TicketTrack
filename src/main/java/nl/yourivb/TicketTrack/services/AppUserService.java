package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.AppUser.AppUserDto;
import nl.yourivb.TicketTrack.dtos.AppUser.AppUserInputDto;
import nl.yourivb.TicketTrack.dtos.AppUser.AppUserPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.AppUserMapper;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import nl.yourivb.TicketTrack.utils.AppUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static nl.yourivb.TicketTrack.utils.AppUtils.allFieldsNull;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;

 AppUserService(AppUserRepository repo,
                            AppUserMapper appUserMapper,
                            PasswordEncoder passwordEncoder) {
    this.appUserRepository = repo;
    this.appUserMapper = appUserMapper;
    this.passwordEncoder = passwordEncoder;
}

    private void checkIfEmailIsUsed(AppUser appUser, Long currentUserId) {
        String email = appUser.getEmail();
        Optional<AppUser> existingUser = appUserRepository.findByEmail(email);

        if (existingUser.isPresent() && !existingUser.get().getId().equals(currentUserId)) {
            throw new BadRequestException("Email address already registered to an account.");
        }
    }


    private void validatePasswordPolicy(String password) {
        if (password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long");
        }
//        if (!password.matches(".*[A-Z].*")) {
//            throw new BadRequestException("Password must contain at least one uppercase letter");
//        }
//        if (!password.matches(".*[a-z].*")) {
//            throw new BadRequestException("Password must contain at least one lowercase letter");
//        }
//        if (!password.matches(".*[!@#$%^&*].*")) {
//            throw new BadRequestException("Password must contain at least one special character (!@#$%^&*)");
//        }
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

        validatePasswordPolicy(dto.getPassword());
        AppUser appUser = appUserMapper.toModel(dto);
        checkIfEmailIsUsed(appUser, appUser.getId());
        
        appUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        appUserRepository.save(appUser);
        return appUserMapper.toDto(appUser);
    }

    public AppUserDto updateUser(Long id, AppUserInputDto dto) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User " + id + " not found"));

        appUserMapper.updateAppUserFromDto(dto, appUser);
        checkIfEmailIsUsed(appUser, id);
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
        if (dto.getEmail() != null) {
            checkIfEmailIsUsed(appUser, id);
        }

        appUserRepository.save(appUser);

        return appUserMapper.toDto(appUser);
    }

    public void deleteUser(Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("User " + id + " not found"));

        appUserRepository.deleteById(id);
    }
}
