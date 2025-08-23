package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.AppUser.AppUserDto;
import nl.yourivb.TicketTrack.dtos.AppUser.AppUserInputDto;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.AppUserMapper;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.models.Role;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {
    @Mock
    AppUserRepository appUserRepository;

    @Mock
    AppUserMapper appUserMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Captor
    ArgumentCaptor<AppUser> appUserCaptor;

    @InjectMocks
    AppUserService appUserService;

    @Test
    void getAllUsers() {
        // Arrange
        AppUser userEntity1 = new AppUser(); userEntity1.setId(1L);
        AppUser userEntity2 = new AppUser(); userEntity2.setId(2L);

        when(appUserRepository.findAll()).thenReturn(List.of(userEntity1, userEntity2));

        AppUserDto userDto1 = new AppUserDto(); userDto1.setId(1L);
        AppUserDto userDto2 = new AppUserDto(); userDto2.setId(2L);
        when(appUserMapper.toDto(userEntity1)).thenReturn(userDto1);
        when(appUserMapper.toDto(userEntity2)).thenReturn(userDto2);

        // Act
        List<AppUserDto> result = appUserService.getAllUsers();

        // Assert (the content itself)
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        // Assert (collaboration)
        verify(appUserRepository, times(1)).findAll();
        verify(appUserMapper).toDto(userEntity1);
        verify(appUserMapper).toDto(userEntity2);
    }

    @Test
    void getUserById() {
        // Arrange
        AppUser entity = new AppUser(); entity.setId(1L);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(entity));

        AppUserDto dto = new AppUserDto(); dto.setId(1L);
        when(appUserMapper.toDto(entity)).thenReturn(dto);

        // Act
        AppUserDto result = appUserService.getUserById(1L);

        // Assert (content)
        assertEquals(1L, result.getId());

        // Assert (verify)
        verify(appUserRepository, times(1)).findById(1L);
        verify(appUserMapper, times(1)).toDto(entity);
    }

    @Test
    void getUserByIdNotFound() {
        // Arrange
        when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> appUserService.getUserById(1L));

        // Assert (collaboration)
        verify(appUserRepository, times(1)).findById(1L);
        verifyNoInteractions(appUserMapper);
    }

    @Test
    void createUser() {
        // Arrange
        AppUserInputDto inputDto = new AppUserInputDto();
        inputDto.setPassword("12345678");
        inputDto.setRoleId(3L);

        AppUser entity = new AppUser();
        AppUserDto outputDto = new AppUserDto();

        when(appUserMapper.toDto(entity)).thenReturn(outputDto);

        // mocks the mapper so we can validate role id on inputDto after Act phase.
        when(appUserMapper.toModel(any(AppUserInputDto.class))).thenAnswer(inv -> {
            AppUserInputDto dto = inv.getArgument(0);
            AppUser returnEntity = entity;
            Role role = new Role();
            role.setId(dto.getRoleId());
            returnEntity.setRole(role);

            return returnEntity;
        });

        // repo gives back the same object, no actual save.
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        // testing the actual encoder is done in an integrating test.
        when(passwordEncoder.encode(inputDto.getPassword())).thenReturn("hashed123");

        // Act
        AppUserDto result = appUserService.createUser(inputDto);

        // Assert (contract)
        assertSame(outputDto, result);

        // Assert (repo check)
        verify(appUserRepository).save(appUserCaptor.capture());
        AppUser saved = appUserCaptor.getValue();

        assertEquals(3L, saved.getRole().getId());
        assertEquals("hashed123", saved.getPassword());

        // Assert (collaboration)
        verify(appUserMapper).toModel(inputDto);
        verify(appUserMapper).toDto(entity);
    }

    @Test
    void updateUser() {
    }

    @Test
    void patchUser() {
    }

    @Test
    void deleteUser() {
        // Arrange
        AppUser entity = new AppUser(); entity.setId(1L);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Act
        appUserService.deleteUser(1L);

        // Assert
        verify(appUserRepository, times(1)).findById(1L);
        verify(appUserRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(appUserRepository);
    }

    @Test
    void deleteUserNotFound() {
        // Arrange
        when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> appUserService.deleteUser(1L));

        // Assert (collaboration)
        verify(appUserRepository, times(1)).findById(1L);
        verify(appUserRepository, never()).deleteById(any());
        verifyNoMoreInteractions(appUserRepository);
    }
}