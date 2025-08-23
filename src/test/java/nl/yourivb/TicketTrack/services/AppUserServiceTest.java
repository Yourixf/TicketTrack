package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.AppUser.AppUserDto;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.AppUserMapper;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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