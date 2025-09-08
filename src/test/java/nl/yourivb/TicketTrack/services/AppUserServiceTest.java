package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.appuser.AppUserDto;
import nl.yourivb.TicketTrack.dtos.appuser.AppUserInputDto;
import nl.yourivb.TicketTrack.dtos.appuser.AppUserPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.AppUserMapper;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.models.Role;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import nl.yourivb.TicketTrack.utils.AppUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void createUserWithTakenEmail() {
        // Arrange
        AppUser originalUser = new AppUser();
        originalUser.setEmail("john@wick.com");
        originalUser.setId(1L);

        AppUserInputDto inputDto = new AppUserInputDto();
        inputDto.setEmail("john@wick.com");
        inputDto.setPassword("12345678");

        AppUser mappedEntity = new AppUser();

        when(appUserRepository.findByEmail("john@wick.com")).thenReturn(Optional.of(originalUser));

        when(appUserMapper.toModel(any(AppUserInputDto.class))).thenAnswer(inv -> {
            AppUserInputDto dto = inv.getArgument(0);
            AppUser returnEntity = mappedEntity;
            returnEntity.setEmail(dto.getEmail());

            return returnEntity;
        });

        // Act & Assert
        assertThrows(BadRequestException.class, () -> appUserService.createUser(inputDto));

        // Assert (collaboration)
        verify(appUserRepository, times(1)).findByEmail(inputDto.getEmail());
        verify(appUserRepository, never()).save(any());
        verify(appUserMapper, never()).toDto(any());
        verifyNoMoreInteractions(appUserRepository, appUserMapper, passwordEncoder);
    }

    @Test
    void createUserWithInvalidPassword() {
        // Arrange
        AppUserInputDto inputDto = new AppUserInputDto();
        inputDto.setPassword("123456"); // current policy is minimal length of 8 characters.

        // Act & Assert
        assertThrows(BadRequestException.class, () -> appUserService.createUser(inputDto));

        // Assert (collaboration)
        verify(appUserRepository, never()).save(any());
        verify(appUserMapper, never()).toDto(any());
        verifyNoMoreInteractions(appUserRepository, appUserMapper, passwordEncoder);
    }

    @Test
    void updateUser() {
        // Arrange
        AppUser originalEntity = new AppUser();
        originalEntity.setId(1L);
        originalEntity.setName("Johnwick");

        AppUserInputDto newInputDto = new AppUserInputDto();
        newInputDto.setName("John Wick");

        AppUserDto outputDto = new AppUserDto();

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(originalEntity));

        doAnswer(inv -> {
            AppUserInputDto dto = inv.getArgument(0);
            AppUser entity = inv.getArgument(1);
            entity.setName(dto.getName());
            return null;
        }).when(appUserMapper).updateAppUserFromDto(eq(newInputDto), same(originalEntity));

        when(appUserRepository.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));
        when(appUserMapper.toDto(originalEntity)).thenReturn(outputDto);

        // Act
        AppUserDto result = appUserService.updateUser(1L, newInputDto);

        // Assert (contract)
        assertEquals(outputDto, result);

        // Assert (repo validation)
        verify(appUserRepository).save(appUserCaptor.capture());
        AppUser saved = appUserCaptor.getValue();

        assertEquals("John Wick", saved.getName());

        // Assert (collaboration)
        verify(appUserRepository, times(1)).findById(1L);
        verify(appUserMapper, times(1)).updateAppUserFromDto(newInputDto, originalEntity);
        verify(appUserRepository, times(1)).findByEmail(any());
        verify(appUserRepository, times(1)).save(any(AppUser.class));
        verify(appUserMapper, times(1)).toDto(originalEntity);
    }

    @Test
    void updateUserWithTakenEmail() {
        // Arrange
        AppUser originalEntity = new AppUser();
        originalEntity.setId(1L);
        originalEntity.setEmail("john@wck.com");

        AppUser otherEntity = new AppUser();
        otherEntity.setId(2L);
        otherEntity.setEmail("john@wick.com");

        AppUserInputDto newInputDto = new AppUserInputDto();
        newInputDto.setEmail("john@wick.com");

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(originalEntity));

        doAnswer(inv -> {
            AppUserInputDto dto = inv.getArgument(0);
            AppUser entity = inv.getArgument(1);
            entity.setEmail(dto.getEmail());
            return null;
        }).when(appUserMapper).updateAppUserFromDto(eq(newInputDto), same(originalEntity));

        when(appUserRepository.findByEmail(newInputDto.getEmail())).thenReturn(Optional.of(otherEntity));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> appUserService.updateUser(1L, newInputDto));

        // Assert (collaboration)
        verify(appUserRepository, times(1)).findById(1L);
        verify(appUserMapper, times(1)).updateAppUserFromDto(newInputDto, originalEntity);
        verify(appUserRepository, times(1)).findByEmail(any());
        verify(appUserRepository, never()).save(any(AppUser.class));
        verify(appUserMapper, never()).toDto(any(AppUser.class));
        verifyNoMoreInteractions(appUserRepository,appUserMapper);
    }

    @Test
    void patchUser() {
        // Arrange
        AppUser originalEntity = new AppUser();
        originalEntity.setId(1L);
        originalEntity.setName("Johnwick");
        originalEntity.setEmail("john@wck.com");

        AppUserPatchDto newPatchDto = new AppUserPatchDto();
        newPatchDto.setName("John Wick");
        newPatchDto.setEmail("john@wick.com");

        AppUserDto outputDto = new AppUserDto();

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(originalEntity));

        doAnswer(inv -> {
            AppUserPatchDto dto = inv.getArgument(0);
            AppUser entity = inv.getArgument(1);
            entity.setName(dto.getName());
            entity.setEmail(dto.getEmail());
            return null;
        }).when(appUserMapper).patchAppUserFromDto(eq(newPatchDto), same(originalEntity));

        when(appUserRepository.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));
        when(appUserMapper.toDto(originalEntity)).thenReturn(outputDto);

        try (var mocked = Mockito.mockStatic(AppUtils.class)) {
            mocked.when(() -> AppUtils.allFieldsNull(newPatchDto)).thenReturn(false);
            // Act
            AppUserDto result = appUserService.patchUser(1L, newPatchDto);

            // Assert (contract)
            assertEquals(outputDto, result);

            // Assert (repo validation)
            verify(appUserRepository).save(appUserCaptor.capture());
            AppUser saved = appUserCaptor.getValue();

            assertEquals("John Wick", saved.getName());
            assertEquals("john@wick.com", saved.getEmail());

            // Assert (collaboration)
            verify(appUserRepository, times(1)).findById(1L);
            verify(appUserMapper, times(1)).patchAppUserFromDto(newPatchDto, originalEntity);
            verify(appUserRepository, times(1)).findByEmail(any());
            verify(appUserRepository, times(1)).save(any(AppUser.class));
            verify(appUserMapper, times(1)).toDto(originalEntity);

            // Static verify
            mocked.verify(() -> AppUtils.allFieldsNull(eq(newPatchDto)));
        }
    }

    @Test
    void patchUserNoValidFields() {
        // Arrange
        AppUser originalEntity = new AppUser();
        originalEntity.setId(1L);

        AppUserPatchDto newPatchDto = new AppUserPatchDto();
        AppUserDto outputDto = new AppUserDto();

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(originalEntity));

        try (var mocked = Mockito.mockStatic(AppUtils.class)) {
            mocked.when(() -> AppUtils.allFieldsNull(newPatchDto)).thenReturn(true);
            // Act & Assert

            assertThrows(BadRequestException.class, () -> appUserService.patchUser(1L, newPatchDto));

            // Assert (collaboration)
            verify(appUserRepository, times(1)).findById(1L);
            verify(appUserMapper, never()).patchAppUserFromDto(any(), any());
            verify(appUserRepository, never()).findByEmail(any());
            verify(appUserRepository, never()).save(any(AppUser.class));
            verify(appUserMapper, never()).toDto(originalEntity);
            verifyNoMoreInteractions(appUserRepository,appUserMapper);

            // Static verify
            mocked.verify(() -> AppUtils.allFieldsNull(eq(newPatchDto)));
        }
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