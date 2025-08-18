package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.interaction.InteractionDto;
import nl.yourivb.TicketTrack.mappers.InteractionMapper;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
import nl.yourivb.TicketTrack.utils.AppUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // this says give me an instance of this service, and thus the methods.
    @InjectMocks
    InteractionService interactionService;

    @Test
    void getAllInteractions() {
        // Arrange
        Interaction i1 = new Interaction(); i1.setId(1L);
        Interaction i2 = new Interaction(); i2.setId(2L);

        // this code says: If interactionRepository.findAll()) is called, don't use DB/repository but return a list
        // existing of the above interactions
        when(interactionRepository.findAll()).thenReturn(List.of(i1, i2));

        InteractionDto d1 = new InteractionDto(); d1.setId(1L);
        InteractionDto d2 = new InteractionDto(); d2.setId(2L);
        when(interactionMapper.toDto(i1)).thenReturn(d1);
        when(interactionMapper.toDto(i2)).thenReturn(d2);

        // this code mocks the method that gets the attachment and notes from the interaction.
        try (var mocked = Mockito.mockStatic(AppUtils.class)) {
            mocked.when(() -> AppUtils.enrichWithRelations(any(), anyString(), anyLong(), any(), any()))
                    .then(inv -> null);

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
            mocked.verify(() -> AppUtils.enrichWithRelations(eq(i1), eq("Interaction"), eq(1L), eq(noteRepository), eq(attachmentRepository)));
            mocked.verify(() -> AppUtils.enrichWithRelations(eq(i2), eq("Interaction"), eq(2L), eq(noteRepository), eq(attachmentRepository)));
        }
    }

    @Test
    void getInteractionById() {
        // Arrange
        Interaction i1 = new Interaction(); i1.setId(1L);
        when(interactionRepository.findById(1L)).thenReturn(Optional.of(i1));

        InteractionDto d1 = new InteractionDto(); d1.setId(1L);
        when(interactionMapper.toDto(i1)).thenReturn(d1);

        try (var mocked = Mockito.mockStatic(AppUtils.class)){
            mocked.when(() -> AppUtils.enrichWithRelations(any(), anyString(), anyLong(), any(), any()))
                    .then(inv -> null);

            // Act
            InteractionDto result = interactionService.getInteractionById(1L);

            // Assert (content)
            assertEquals(1L, result.getId());

            // Assert (collaboration)
            verify(interactionRepository, times(1)).findById(1L);
            verify(interactionMapper).toDto(i1);

            // static verify
            mocked.verify(() -> AppUtils.enrichWithRelations( eq(i1), eq("Interaction"), eq(1L), eq(noteRepository), eq(attachmentRepository)));
        }
    }

    @Test
    void addInteraction() {
    }

    @Test
    void updateInteraction() {
    }

    @Test
    void patchInteraction() {
    }

    @Test
    void deleteInteraction() {
    }
}