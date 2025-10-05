package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.interaction.InteractionDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionInputDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.CustomException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.InteractionMapper;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.models.enums.Category;
import nl.yourivb.TicketTrack.models.enums.Channel;
import nl.yourivb.TicketTrack.models.enums.InteractionState;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
import nl.yourivb.TicketTrack.security.AppUserDetails;
import nl.yourivb.TicketTrack.security.SecurityUtils;
import nl.yourivb.TicketTrack.utils.AppUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InteractionServiceTest {
    // this says give me the shell of the class, mocking them myself with when().thenReturn().
    @Mock
    InteractionRepository interactionRepository;
    @Mock
    InteractionMapper interactionMapper;
    @Mock
    NoteRepository noteRepository;
    @Mock
    AttachmentRepository attachmentRepository;

    // this enables you to inspect the entity that went to the repo.
    @Captor
    ArgumentCaptor<Interaction> interactionCaptor;

    // this says give me an instance of this service, and thus the methods.
    @InjectMocks
    InteractionService interactionService;

    @Test
    void getAllInteractions() {
        // Arrange
        AppUser currentUser = new AppUser();
        currentUser.setId(5L);

        AppUser otherUser = new AppUser();
        otherUser.setId(99L);
        // i1 is owned by currentUser
        Interaction i1 = new Interaction();
        i1.setId(1L);
        i1.setOpenedBy(currentUser);
        i1.setOpenedFor(currentUser);

        // i2 is owned by otherUser
        Interaction i2 = new Interaction();
        i2.setId(2L);
        i2.setOpenedBy(otherUser);
        i2.setOpenedFor(otherUser);

        // this code says: If interactionRepository.findAll()) is called, don't use DB/repository but return a list
        // existing of the above interactions
        when(interactionRepository.findAll()).thenReturn(List.of(i1, i2));

        InteractionDto d1 = new InteractionDto(); d1.setId(1L);
        InteractionDto d2 = new InteractionDto(); d2.setId(2L);
        when(interactionMapper.toDto(i1)).thenReturn(d1);
        when(interactionMapper.toDto(i2)).thenReturn(d2);

        // this code mocks the method that gets the attachment and notes from the interaction.
        try (var mockedUtil = Mockito.mockStatic(AppUtils.class);
             var mockedSec = Mockito.mockStatic(SecurityUtils.class)) {
            mockedUtil.when(() -> AppUtils.enrichWithRelations(any(), anyString(), anyLong(), any(), any()))
                    .then(inv -> null);


            mockedUtil.when(() -> AppUtils.validateTicketAccess(anyLong(), anyLong()))
                    .thenAnswer(inv -> null); // of .then() met recente Mockito versie


            mockedSec.when(SecurityUtils::getCurrentUserId).thenReturn(5L);
            mockedSec.when(() -> SecurityUtils.hasRole("CUSTOMER")).thenReturn(true);
            // Act
            List<InteractionDto> result = interactionService.getAllInteractions();

            // Assert (the content itself)
            assertEquals(2, result.size()); // expects 2 interactions in the list.
            assertEquals(1L, result.get(0).getId()); // expects interaction 1 to have ID 1.
            assertEquals(2L, result.get(1).getId());

            // Assert (the collaboration)

            // this says, ensures the find all method has been called once in the unit test.
            verify(interactionRepository, times(1)).findAll();

            verify(interactionMapper).toDto(i1);
            verify(interactionMapper).toDto(i2);

            // static verify, makes sure enrich method has been used twice (which it does in the actual service method).
            mockedUtil.verify(() -> AppUtils.enrichWithRelations(eq(i1), eq("Interaction"), eq(1L), eq(noteRepository), eq(attachmentRepository)));
            mockedUtil.verify(() -> AppUtils.enrichWithRelations(eq(i2), eq("Interaction"), eq(2L), eq(noteRepository), eq(attachmentRepository)));
            mockedUtil.verify(() -> AppUtils.validateTicketAccess(eq(5L), eq(5L)));
            mockedUtil.verify(() -> AppUtils.validateTicketAccess(eq(99L), eq(99L)));

        }
    }

    @Test
    void getAllInteractionsWithCustomerAccessLevel()  {
        // Arrange
        AppUser currentUser = new AppUser();
        currentUser.setId(5L);

        AppUser otherUser = new AppUser();
        otherUser.setId(99L);

        // i1 is owned by currentUser
        Interaction i1 = new Interaction();
        i1.setId(1L);
        i1.setOpenedBy(currentUser);
        i1.setOpenedFor(currentUser);

        // i2 is owned by otherUser
        Interaction i2 = new Interaction();
        i2.setId(2L);
        i2.setOpenedBy(otherUser);
        i2.setOpenedFor(otherUser);

        // this code says: If interactionRepository.findAll()) is called, don't use DB/repository but return a list
        // existing of the above interactions
        when(interactionRepository.findAll()).thenReturn(List.of(i1, i2));

        InteractionDto d1 = new InteractionDto(); d1.setId(1L);
        when(interactionMapper.toDto(i1)).thenReturn(d1);

        // this code mocks the method that gets the attachment and notes from the interaction.
        try (var mockedUtil = Mockito.mockStatic(AppUtils.class);
             var mockedSec = Mockito.mockStatic(SecurityUtils.class)) {

            mockedUtil.when(() -> AppUtils.enrichWithRelations(any(), anyString(), anyLong(), any(), any()))
                    .then(inv -> null);

            mockedUtil.when(() -> AppUtils.validateTicketAccess(eq(99L), any()))
                    .thenThrow(new AccessDeniedException("error"));

            mockedSec.when(SecurityUtils::getCurrentUserId).thenReturn(5L);
            mockedSec.when(() -> SecurityUtils.hasRole("CUSTOMER")).thenReturn(true);

            // Act
            List<InteractionDto> result = interactionService.getAllInteractions();

            // Assert (result)
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getId());

            // Assert (the collaboration)
            // this says, ensures the find all method has been called once in the unit test.
            verify(interactionRepository, times(1)).findAll();

            verify(interactionMapper).toDto(i1);

            // static verify, makes sure enrich method has been used twice (which it does in the actual service method).
            mockedUtil.verify(() -> AppUtils.enrichWithRelations(eq(i1), eq("Interaction"), eq(1L), eq(noteRepository), eq(attachmentRepository)));
        }
    }

    @Test
    void getInteractionById() {
        // Arrange
        AppUser user = new AppUser();
        user.setId(1L);

        Interaction entity = new Interaction();
        entity.setId(1L);
        entity.setOpenedBy(user);
        entity.setOpenedFor(user);

        when(interactionRepository.findById(1L)).thenReturn(Optional.of(entity));

        InteractionDto dto = new InteractionDto();
        dto.setId(1L);
        dto.setOpenedForId(1L);
        dto.setOpenedById(1L);

        when(interactionMapper.toDto(entity)).thenReturn(dto);

        try (var mocked = Mockito.mockStatic(AppUtils.class)){
            mocked.when(() -> AppUtils.enrichWithRelations(any(), anyString(), anyLong(), any(), any()))
                    .then(inv -> null);

            // Act
            InteractionDto result = interactionService.getInteractionById(1L);

            // Assert (contract)
            assertEquals(1L, result.getId());

            // Assert (collaboration)
            verify(interactionRepository, times(1)).findById(1L);
            verify(interactionMapper).toDto(entity);

            // static verify
            mocked.verify(() -> AppUtils.enrichWithRelations( eq(entity), eq("Interaction"), eq(1L), eq(noteRepository), eq(attachmentRepository)));
        }
    }

    @Test
    void getInteractionByIdNotFound() {
        // Arrange
        when(interactionRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> interactionService.getInteractionById(2L));

        // Assert (collaboration)
        verify(interactionRepository).findById(2L);
        verifyNoInteractions(interactionMapper); // makes sure mapper is not called when not intended.
    }

    @Test
    void addInteractionWithCustomerRole() {
        // Arrange
        InteractionInputDto inputDto = new InteractionInputDto();
        inputDto.setOpenedForId(42L);

        Interaction entity = new Interaction();
        InteractionDto outputDto = new InteractionDto();

        when(interactionMapper.toModel(inputDto)).thenReturn(entity);

        // repo gives back the same object, no actual save.
        when(interactionRepository.save(any(Interaction.class))).thenAnswer(inv -> inv.getArgument(0));
        when(interactionMapper.toDto(entity)).thenReturn(outputDto);

        AppUser fakeUser = new AppUser(); fakeUser.setId(42L);

        try (var mockedSec = Mockito.mockStatic(SecurityUtils.class);
             var mockedUtil = Mockito.mockStatic(AppUtils.class)) {

            mockedUtil.when(() -> AppUtils.generateRegistrationNumber("IMS", interactionRepository))
                    .thenReturn("IMS0000001");

            mockedSec.when(SecurityUtils::getCurrentUserDetails)
                    .thenReturn(new AppUserDetails(fakeUser));
            mockedSec.when(() -> SecurityUtils.hasRole("CUSTOMER"))
                    .thenReturn(true);

            // Act
            InteractionDto result = interactionService.addInteraction(inputDto);

            // Assert (output contract)
            assertSame(outputDto, result);

            // Assert (what went to the repo)
            verify(interactionRepository).save(interactionCaptor.capture());
            Interaction saved = interactionCaptor.getValue();

            assertEquals("IMS0000001", saved.getNumber());
            assertEquals(fakeUser, saved.getOpenedBy());
            assertEquals(Channel.SELF_SERVICE, saved.getChannel());
            assertEquals(Category.USER_ASSISTANCE, saved.getCategory());
            assertNull(saved.getOpenedFor(), "openedFor must be null when openedForId == openedBy.id");

            // Assert (collaboration)
            verify(interactionMapper).toModel(inputDto);
            verify(interactionMapper).toDto(entity);

            // Static verify
            mockedUtil.verify(() -> AppUtils.generateRegistrationNumber("IMS", interactionRepository));
            mockedSec.verify(SecurityUtils::getCurrentUserDetails);
            mockedSec.verify(() -> SecurityUtils.hasRole("CUSTOMER"));
        }
    }

    @Test
    void addInteractionWithoutCustomerRole() {
        // Arrange
        InteractionInputDto inputDto = new InteractionInputDto();
        inputDto.setOpenedForId(42L);

        Interaction entity = new Interaction();
        InteractionDto outputDto = new InteractionDto();

        when(interactionMapper.toModel(inputDto)).thenReturn(entity);
        when(interactionRepository.save(any(Interaction.class))).thenAnswer(inv -> inv.getArgument(0));
        when(interactionMapper.toDto(entity)).thenReturn(outputDto);

        AppUser fakeUser = new AppUser(); fakeUser.setId(42L);

        try (var mockedSec = Mockito.mockStatic(SecurityUtils.class);
             var mockedUtil = Mockito.mockStatic(AppUtils.class)) {

            mockedUtil.when(() -> AppUtils.generateRegistrationNumber("IMS", interactionRepository)).thenReturn("IMS0000001");
            mockedSec.when(SecurityUtils::getCurrentUserDetails).thenReturn(new AppUserDetails(fakeUser));
            mockedSec.when(() -> SecurityUtils.hasRole("CUSTOMER")).thenReturn(false);

            // Act
            InteractionDto result = interactionService.addInteraction(inputDto);

            // Assert (output contract)
            assertSame(outputDto, result);

            // Assert (repo validation)
            verify(interactionRepository).save(interactionCaptor.capture());
            Interaction saved = interactionCaptor.getValue();

            assertEquals("IMS0000001", saved.getNumber());
            assertEquals(fakeUser, saved.getOpenedBy());

            // Assert (collaboration)
            verify(interactionMapper).toModel(inputDto);
            verify(interactionMapper).toDto(entity);
            verifyNoMoreInteractions(interactionRepository, interactionMapper);

            // Static verify
            mockedUtil.verify(() -> AppUtils.generateRegistrationNumber("IMS", interactionRepository));
            mockedSec.verify(SecurityUtils::getCurrentUserDetails);
            mockedSec.verify(() -> SecurityUtils.hasRole("CUSTOMER"));
        }
    }


    @Test
    void updateInteraction() {
        // Arrange
        Interaction originalEntity = new Interaction(); originalEntity.setId(1L); originalEntity.setNumber("IMS0000001");
        InteractionInputDto newInputDto = new InteractionInputDto();
        InteractionDto outputDto = new InteractionDto(); outputDto.setId(1L);

        when(interactionRepository.findById(1L)).thenReturn(Optional.of(originalEntity));
        doNothing().when(interactionMapper)
                .updateInteractionFromDto(eq(newInputDto), same(originalEntity));
        when(interactionRepository.save(any(Interaction.class))).thenAnswer(inv -> inv.getArgument(0));
        when(interactionMapper.toDto(originalEntity)).thenReturn(outputDto);


        try (var mocked = Mockito.mockStatic(AppUtils.class)){
            mocked.when(() -> AppUtils.enrichWithRelations(any(), anyString(), anyLong(), any(), any()))
                    .then(inv -> null);

            // Act
            InteractionDto result = interactionService.updateInteraction(1L, newInputDto);

            // Assert (contract)
            assertEquals(outputDto, result);

            // Assert (repo validation)
            verify(interactionRepository).save(interactionCaptor.capture());
            Interaction saved = interactionCaptor.getValue();

            assertEquals("IMS0000001", saved.getNumber());

            // Assert (collaboration)
            verify(interactionRepository, times(1)).findById(1L);
            verify(interactionMapper).updateInteractionFromDto(newInputDto, originalEntity);
            verify(interactionRepository, times(1)).save(any(Interaction.class));
            verify(interactionMapper).toDto(originalEntity);

            // static verify
            mocked.verify(() -> AppUtils.enrichWithRelations( eq(originalEntity), eq("Interaction"), eq(1L), eq(noteRepository), eq(attachmentRepository)));
        }
    }

    @Test
    void updateClosedInteraction() {
        // Arrange
        Interaction originalEntity = new Interaction();
        originalEntity.setId(1L);
        originalEntity.setState(InteractionState.CLOSED);
        originalEntity.setClosed(LocalDateTime.now());

        InteractionInputDto newInputDto = new InteractionInputDto();
        newInputDto.setState(InteractionState.NEW);

        when(interactionRepository.findById(1L)).thenReturn(Optional.of(originalEntity));

        // this 'mocks' an actual mapper so later we can compare state in interaction
        doAnswer(inv -> {
            InteractionInputDto dto = inv.getArgument(0);
            Interaction entity = inv.getArgument(1);
            entity.setState(dto.getState());
            return null;
        }).when(interactionMapper).updateInteractionFromDto(eq(newInputDto), same(originalEntity));

        // Act & Assert
        assertThrows(CustomException.class, () -> interactionService.updateInteraction(1L, newInputDto));

        // Assert (collaboration)
        verify(interactionRepository, times(1)).findById(1L);
        verify(interactionRepository, never()).save(any());
        verify(interactionMapper, never()).toDto(any());
        verifyNoMoreInteractions(interactionRepository, interactionMapper);
    }

    @Test
    void patchInteraction() {
        // Arrange
        Interaction originalEntity = new Interaction(); originalEntity.setId(1L); originalEntity.setNumber("IMS0000001");
        InteractionPatchDto newPatchDto = new InteractionPatchDto();
        InteractionDto outputDto = new InteractionDto(); outputDto.setId(1L);

        when(interactionRepository.findById(1L)).thenReturn(Optional.of(originalEntity));
        doNothing().when(interactionMapper)
                .patchInteractionFromDto(eq(newPatchDto), same(originalEntity));
        when(interactionRepository.save(any(Interaction.class))).thenAnswer(inv -> inv.getArgument(0));
        when(interactionMapper.toDto(originalEntity)).thenReturn(outputDto);


        try (var mocked = Mockito.mockStatic(AppUtils.class)) {
            mocked.when(() -> AppUtils.enrichWithRelations(any(), anyString(), anyLong(), any(), any()))
                    .then(inv -> null);

            mocked.when(() -> AppUtils.allFieldsNull(newPatchDto)).thenReturn(false);

            // Act
            InteractionDto result = interactionService.patchInteraction(1L, newPatchDto);

            // Assert (contract)
            assertEquals(outputDto, result);

            // Assert (repo validation)
            verify(interactionRepository).save(interactionCaptor.capture());
            Interaction saved = interactionCaptor.getValue();

            assertEquals("IMS0000001", saved.getNumber());

            // Assert (collaboration)
            verify(interactionRepository, times(1)).findById(1L);
            verify(interactionMapper).patchInteractionFromDto(newPatchDto, originalEntity);
            verify(interactionRepository, times(1)).save(any(Interaction.class));
            verify(interactionMapper).toDto(originalEntity);

            // static verify
            mocked.verify(() -> AppUtils.allFieldsNull(eq(newPatchDto)));
            mocked.verify(() -> AppUtils.enrichWithRelations(eq(originalEntity), eq("Interaction"), eq(1L), eq(noteRepository), eq(attachmentRepository)));
        }
    }

    @Test
    void patchInteractionNoValidFields() {
        // Arrange
        Interaction originalEntity = new Interaction(); originalEntity.setId(1L);
        InteractionPatchDto newPatchDto = new InteractionPatchDto();

        when(interactionRepository.findById(1L)).thenReturn(Optional.of(originalEntity));

        try (var mocked = Mockito.mockStatic(AppUtils.class)) {

            mocked.when(() -> AppUtils.allFieldsNull(newPatchDto)).thenReturn(true);

            // Act & Assert
            assertThrows(BadRequestException.class, () -> interactionService.patchInteraction(1L, newPatchDto));

            // Assert (collaboration)
            verify(interactionRepository, times(1)).findById(1L);
            verify(interactionRepository, never()).save(any());
            verify(interactionMapper, never()).patchInteractionFromDto(any(), any());
            verifyNoMoreInteractions(interactionRepository, interactionMapper);

            // Static verify
            mocked.verify(() -> AppUtils.allFieldsNull(eq(newPatchDto)));
            mocked.verify(() -> AppUtils.allFieldsNull(eq(newPatchDto)));
        }
    }

    @Test
    void deleteInteraction() {
        // Arrange
        Interaction entity = new Interaction(); entity.setId(1L);
        when(interactionRepository.findById(1L)).thenReturn(Optional.of(entity));

        // Act
        interactionService.deleteInteraction(1L);

        // Assert (collaboration)
        verify(interactionRepository, times(1)).findById(1L);
        verify(interactionRepository).deleteById(1L);
        verifyNoMoreInteractions(interactionRepository);
    }

    @Test
    void deleteInteractionNotFound() {
        // Arrange
        when(interactionRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> interactionService.deleteInteraction(2L));

        // Assert (collaboration)
        verify(interactionRepository).findById(2L);
        verify(interactionRepository, never()).deleteById(any());
        verifyNoMoreInteractions(interactionRepository);
    }
}