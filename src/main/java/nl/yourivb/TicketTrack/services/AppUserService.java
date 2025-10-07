package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.appuser.AppUserDto;
import nl.yourivb.TicketTrack.dtos.appuser.AppUserInputDto;
import nl.yourivb.TicketTrack.dtos.appuser.AppUserPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.CustomException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.AppUserMapper;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import nl.yourivb.TicketTrack.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private void checkIfEmailIsUsed(String rawEmail, Long excludeUserId) {
        String email = rawEmail == null ? null : rawEmail.trim().toLowerCase();

        appUserRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getId().equals(excludeUserId)) {
                throw new CustomException("Email address already registered to an account.", HttpStatus.CONFLICT);
            }
        });
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


    private void verifyAccessToModifyUser(Long targetUserId) {
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        AppUser currentUser = SecurityUtils.getCurrentUserDetails().getAppUser();
        if (!isAdmin && !currentUser.getId().equals(targetUserId)) {
            throw new AccessDeniedException("You can only change your own account.");
        }
    }

    private void validateAccessToModifyRole(Long roleId) {
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        if (!isAdmin && roleId != null) {
            throw new AccessDeniedException("Only admins can change user roles.");
        }
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
        checkIfEmailIsUsed(appUser.getEmail(), null);
        
        appUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        appUserRepository.save(appUser);
        return appUserMapper.toDto(appUser);
    }

    public AppUserDto updateUser(Long id, AppUserInputDto dto) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User " + id + " not found"));

        String originalPassword = appUser.getPassword();

        verifyAccessToModifyUser(id);

        // only admins are allowed to change roles
        validateAccessToModifyRole(dto.getRoleId());

        // this prevents roleId being set to null if not updated.
        if (dto.getRoleId() == null && appUser.getRole() != null) {
            dto.setRoleId(appUser.getRole().getId());
        }

        appUserMapper.updateAppUserFromDto(dto, appUser);
        checkIfEmailIsUsed(dto.getEmail(), id);

        if (dto.getPassword() != null) {
            validatePasswordPolicy(dto.getPassword());
            appUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else if (dto.getPassword() == null) {
            appUser.setPassword(originalPassword);
        }

        appUserRepository.save(appUser);

        return appUserMapper.toDto(appUser);
    }

    public AppUserDto patchUser(Long id, AppUserPatchDto dto) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User " + id + " not found"));

        String originalPassword = appUser.getPassword();


        verifyAccessToModifyUser(id);

        // only admins are allowed to change roles
        validateAccessToModifyRole(dto.getRoleId());

        // this prevents roleId being set to null if not updated.
        if (dto.getRoleId() == null && appUser.getRole() != null) {
            dto.setRoleId(appUser.getRole().getId());
        }

        if (allFieldsNull(dto)) {
            throw new BadRequestException("No valid fields provided for patch");
        }

        appUserMapper.patchAppUserFromDto(dto, appUser);
        if (dto.getEmail() != null) {
            checkIfEmailIsUsed(appUser.getEmail(), id);
        }

        if (dto.getPassword() != null) {
            validatePasswordPolicy(dto.getPassword());
            appUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else if (dto.getPassword() == null) {
            appUser.setPassword(originalPassword);
        }

        appUserRepository.save(appUser);

        return appUserMapper.toDto(appUser);
    }

    public void deleteUser(Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("User " + id + " not found"));

        appUserRepository.deleteById(id);
    }
}
