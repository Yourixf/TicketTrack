package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.assignmentgroup.AssignmentGroupDto;
import nl.yourivb.TicketTrack.dtos.assignmentgroup.AssignmentGroupInputDto;
import nl.yourivb.TicketTrack.dtos.assignmentgroup.AssignmentGroupPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.AssignmentGroupMapper;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.models.AssignmentGroup;
import nl.yourivb.TicketTrack.repositories.AssignmentGroupRepository;
import nl.yourivb.TicketTrack.security.AppUserDetails;
import nl.yourivb.TicketTrack.security.SecurityUtils;
import nl.yourivb.TicketTrack.utils.AppUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentGroupServiceTest {
    @Mock
    AssignmentGroupRepository assignmentGroupRepository;

    @Mock
    AssignmentGroupMapper assignmentGroupMapper;

    @Captor
    ArgumentCaptor<AssignmentGroup> assignmentGroupCaptor;

    @InjectMocks
    AssignmentGroupService assignmentGroupService;

    @Test
    void getAllAssignmentGroups() {
        // Arrange
        AssignmentGroup assignmentGroupEntity1 = new AssignmentGroup(); assignmentGroupEntity1.setId(1L);
        AssignmentGroup assignmentGroupEntity2 = new AssignmentGroup(); assignmentGroupEntity2.setId(2L);

        AssignmentGroupDto assignmentGroupDto1 = new AssignmentGroupDto(); assignmentGroupDto1.setId(1L);
        AssignmentGroupDto assignmentGroupDto2 = new AssignmentGroupDto(); assignmentGroupDto2.setId(2L);

        when(assignmentGroupRepository.findAll()).thenReturn(List.of(assignmentGroupEntity1, assignmentGroupEntity2));
        when(assignmentGroupMapper.toDto(assignmentGroupEntity1)).thenReturn(assignmentGroupDto1);
        when(assignmentGroupMapper.toDto(assignmentGroupEntity2)).thenReturn(assignmentGroupDto2);

        // Act
        List<AssignmentGroupDto> result = assignmentGroupService.getAllAssignmentGroups();

        // Assert (the content itself)
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        // Assert (collaboration)
        verify(assignmentGroupRepository, times(1)).findAll();
        verify(assignmentGroupMapper).toDto(assignmentGroupEntity1);
        verify(assignmentGroupMapper).toDto(assignmentGroupEntity2);
    }

    @Test
    void getAssignmentGroupById() {
        // Arrange
        AssignmentGroup entity = new AssignmentGroup(); entity.setId(1L);
        AssignmentGroupDto dto = new AssignmentGroupDto(); dto.setId(1L);

        when(assignmentGroupRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(assignmentGroupMapper.toDto(entity)).thenReturn(dto);

        // Act
        AssignmentGroupDto result = assignmentGroupService.getAssignmentGroupById(1L);

        // Assert (content)
        assertEquals(1L, result.getId());

        // Assert (collaboration)
        verify(assignmentGroupRepository, times(1)).findById(1L);
        verify(assignmentGroupMapper, times(1)).toDto(entity);

    }

    @Test
    void getAssignmentGroupByIdNotFound() {
        // Arrange
        when(assignmentGroupRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> assignmentGroupService.getAssignmentGroupById(1L));

        // Assert (collaboration)
        verify(assignmentGroupRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(assignmentGroupMapper);
    }

    @Test
    void addAssignmentGroup() {
        // Arrange
        AssignmentGroupInputDto inputDto = new AssignmentGroupInputDto();
        inputDto.setName("first line support");

        AssignmentGroup entity = new AssignmentGroup();
        AssignmentGroupDto outputDto = new AssignmentGroupDto();

        AppUser fakeUser = new AppUser(); fakeUser.setId(42L);

        when(assignmentGroupMapper.toDto(entity)).thenReturn(outputDto);

        // mocking the mapper so we can validate name after act phase
        when(assignmentGroupMapper.toModel(any(AssignmentGroupInputDto.class))).thenAnswer(inv -> {
            AssignmentGroupInputDto dto = inv.getArgument(0);
            AssignmentGroup returnEntity = entity;
            returnEntity.setName(dto.getName());

            return returnEntity;
        });

        // repo gives back the same object, no actual save.
        when(assignmentGroupRepository.save(any(AssignmentGroup.class))).thenAnswer(inv -> inv.getArgument(0));

        try (var mockedSec = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSec.when(SecurityUtils::getCurrentUserDetails).thenReturn(new AppUserDetails(fakeUser));

            // Act
            AssignmentGroupDto result = assignmentGroupService.addAssignmentGroup(inputDto);

            // Assert (contract)
            assertSame(outputDto, result);

            // Assert (repo check)
            verify(assignmentGroupRepository).save(assignmentGroupCaptor.capture());
            AssignmentGroup saved = assignmentGroupCaptor.getValue();

            assertEquals("first line support", saved.getName());

            // Assert (collaboration)
            verify(assignmentGroupMapper).toModel(inputDto);
            verify(assignmentGroupMapper).toDto(entity);

            // Static verify
            mockedSec.verify(SecurityUtils::getCurrentUserDetails);
        }
    }

    @Test
    void updateAssignmentGroup() {
        // Arrange
        AssignmentGroup originalEntity = new AssignmentGroup();
        originalEntity.setId(1L);
        originalEntity.setName("first line it suport");

        AssignmentGroupInputDto newInputDto = new AssignmentGroupInputDto();
        newInputDto.setName("First Line IT Support");

        AssignmentGroupDto outputDto = new AssignmentGroupDto();

        when(assignmentGroupRepository.findById(1L)).thenReturn(Optional.of(originalEntity));

        doAnswer(inv -> {
            AssignmentGroupInputDto dto = inv.getArgument(0);
            AssignmentGroup entity = inv.getArgument(1);
            entity.setName(dto.getName());
            return null;
        }).when(assignmentGroupMapper).updateAssignmentGroupFromDto(eq(newInputDto), same(originalEntity));

        when(assignmentGroupRepository.save(any(AssignmentGroup.class))).thenAnswer(inv -> inv.getArgument(0));
        when(assignmentGroupMapper.toDto(originalEntity)).thenReturn(outputDto);

        // Act
        AssignmentGroupDto result = assignmentGroupService.updateAssignmentGroup(1L, newInputDto);

        // Assert (contract)
        assertEquals(outputDto, result);

        // Assert (repo validation)
        verify(assignmentGroupRepository).save(assignmentGroupCaptor.capture());
        AssignmentGroup saved = assignmentGroupCaptor.getValue();

        assertEquals("First Line IT Support", saved.getName());

        // Assert collaboration
        verify(assignmentGroupRepository, times(1)).findById(1L);
        verify(assignmentGroupMapper, times(1)).updateAssignmentGroupFromDto(newInputDto, originalEntity);
        verify(assignmentGroupRepository, times(1)).save(any(AssignmentGroup.class));
        verify(assignmentGroupMapper, times(1)).toDto(originalEntity);
    }

    @Test
    void updateAssignmentGroupNotFound() {
        // Arrange
        when(assignmentGroupRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> assignmentGroupService.updateAssignmentGroup(1L, any()));

        // Assert (collaboration)
        verify(assignmentGroupRepository, times(1)).findById(1L);
        verify(assignmentGroupMapper, never()).updateAssignmentGroupFromDto(any(), any());
        verify(assignmentGroupRepository, never()).save(any(AssignmentGroup.class));
        verify(assignmentGroupMapper, never()).toDto(any());
        verifyNoMoreInteractions(assignmentGroupMapper);
    }

    @Test
    void patchAssignmentGroup() {
        // Arrange
        AssignmentGroup originalEntity = new AssignmentGroup(); originalEntity.setId(1L);
        originalEntity.setName("first line it suport");

        AssignmentGroupPatchDto newPatchDto = new AssignmentGroupPatchDto();
        newPatchDto.setName("First Line IT Support");

        AssignmentGroupDto outputDto = new AssignmentGroupDto(); outputDto.setId(1L);

        when(assignmentGroupRepository.findById(1L)).thenReturn(Optional.of(originalEntity));

        doAnswer(inv -> {
            AssignmentGroupPatchDto dto = inv.getArgument(0);
            AssignmentGroup entity = inv.getArgument(1);
            entity.setName(dto.getName());
            return null;
        }).when(assignmentGroupMapper).patchAssignmentGroupFromDto(eq(newPatchDto), same(originalEntity));


        when(assignmentGroupRepository.save(any(AssignmentGroup.class))).thenAnswer(inv -> inv.getArgument(0));
        when(assignmentGroupMapper.toDto(originalEntity)).thenReturn(outputDto);


        try (var mocked = Mockito.mockStatic(AppUtils.class)) {
            mocked.when(() -> AppUtils.allFieldsNull(newPatchDto)).thenReturn(false);

            // Act
            AssignmentGroupDto result = assignmentGroupService.patchAssignmentGroup(1L, newPatchDto);

            // Assert (contract)
            assertEquals(outputDto, result);

            // Assert (repo validation)
            verify(assignmentGroupRepository).save(assignmentGroupCaptor.capture());
            AssignmentGroup saved = assignmentGroupCaptor.getValue();

            assertEquals("First Line IT Support", saved.getName());

            // Assert (collaboration)
            verify(assignmentGroupRepository, times(1)).findById(1L);
            verify(assignmentGroupMapper).patchAssignmentGroupFromDto(newPatchDto, originalEntity);
            verify(assignmentGroupRepository, times(1)).save(any(AssignmentGroup.class));
            verify(assignmentGroupMapper).toDto(originalEntity);

            // static verify
            mocked.verify(() -> AppUtils.allFieldsNull(eq(newPatchDto)));

        }
    }

    @Test
    void patchAssignmentGroupNoValidFields() {
        // Arrange
        AssignmentGroup originalEntity = new AssignmentGroup(); originalEntity.setId(1L);
        AssignmentGroupPatchDto newPatchDto = new AssignmentGroupPatchDto();

        when(assignmentGroupRepository.findById(1L)).thenReturn(Optional.of(originalEntity));

        try (var mocked = Mockito.mockStatic(AppUtils.class)) {

            mocked.when(() -> AppUtils.allFieldsNull(newPatchDto)).thenReturn(true);

            // Act & Assert
            assertThrows(BadRequestException.class, () -> assignmentGroupService.patchAssignmentGroup(1L, newPatchDto));

            // Assert (collaboration)
            verify(assignmentGroupRepository, times(1)).findById(1L);
            verify(assignmentGroupRepository, never()).save(any());
            verify(assignmentGroupMapper, never()).patchAssignmentGroupFromDto(any(), any());
            verifyNoMoreInteractions(assignmentGroupRepository, assignmentGroupMapper);

            // Static verify
            mocked.verify(() -> AppUtils.allFieldsNull(eq(newPatchDto)));
            mocked.verify(() -> AppUtils.allFieldsNull(eq(newPatchDto)));
        }
    }

    @Test
    void patchAssignmentGroupNotFound() {
        // Arrange
        when(assignmentGroupRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> assignmentGroupService.patchAssignmentGroup(1L, any()));

        // Assert (collaboration)
        verify(assignmentGroupRepository, times(1)).findById(1L);
        verify(assignmentGroupMapper, never()).patchAssignmentGroupFromDto(any(), any());
        verify(assignmentGroupRepository, never()).save(any(AssignmentGroup.class));
        verify(assignmentGroupMapper, never()).toDto(any());
        verifyNoMoreInteractions(assignmentGroupMapper);
    }

    @Test
    void deleteAssignmentGroup() {
        // Arrange
        AssignmentGroup entity = new AssignmentGroup(); entity.setId(1L);
        when(assignmentGroupRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Act
        assignmentGroupService.deleteAssignmentGroup(1L);

        // Assert (collaboration)
        verify(assignmentGroupRepository, times(1)).findById(1L);
        verify(assignmentGroupRepository).deleteById(1L);
        verifyNoMoreInteractions(assignmentGroupRepository);
    }

    @Test
    void deleteAssignmentGroupNotFound() {
        // Arrange
        when(assignmentGroupRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> assignmentGroupService.deleteAssignmentGroup(2L));

        // Assert (collaboration)
        verify(assignmentGroupRepository).findById(2L);
        verify(assignmentGroupRepository, never()).deleteById(any());
        verifyNoMoreInteractions(assignmentGroupRepository);
    }
}