package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.interaction.InteractionDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionInputDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.InteractionMapper;
import nl.yourivb.TicketTrack.models.Attachment;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.models.Note;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static nl.yourivb.TicketTrack.utils.AppUtils.allFieldsNull;
import static nl.yourivb.TicketTrack.utils.AppUtils.generateRegistrationNumber;


@Service
public class InteractionService {
    private final InteractionRepository interactionRepository;
    private final InteractionMapper interactionMapper;
    private final NoteRepository noteRepository;
    private final AttachmentRepository attachmentRepository;

    public InteractionService(InteractionRepository interactionRepository,
                              InteractionMapper interactionMapper,
                              NoteRepository noteRepository,
                              AttachmentRepository attachmentRepository) {
        this.interactionRepository = interactionRepository;
        this.interactionMapper = interactionMapper;
        this.noteRepository = noteRepository;
        this.attachmentRepository = attachmentRepository;
    }

    public List<InteractionDto> getAllInteractions() {

        // this finds alls interactions, loops throug each, gets and sets the corresponding note & atta list.
        return interactionRepository.findAll()
                .stream()
                .map(interaction -> {
                    interaction.setNotes(noteRepository.findByNoteableTypeAndNoteableId("Interaction", interaction.getId()));
                    interaction.setAttachments(attachmentRepository.findByAttachableTypeAndAttachableId("Interaction", interaction.getId()));
                    return interactionMapper.toDto(interaction);
                })
                .toList();
    }

    public InteractionDto getInteractionById(Long id) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Interaction " + id + " not found"));

        List<Note> notes = noteRepository.findByNoteableTypeAndNoteableId("Interaction", interaction.getId());
        List<Attachment> attachments = attachmentRepository.findByAttachableTypeAndAttachableId("Interaction", interaction.getId());

        interaction.setNotes(notes);
        interaction.setAttachments(attachments);

        return interactionMapper.toDto(interaction);

    }

    public InteractionDto addInteraction(InteractionInputDto dto) {
        Interaction interaction = interactionMapper.toModel(dto);

        interaction.setNumber(generateRegistrationNumber("IMS", interactionRepository));
        interactionRepository.save(interaction);

        return interactionMapper.toDto(interaction);
    }

    public InteractionDto updateInteraction(Long id, InteractionInputDto newInteraction) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Interaction " + id + " not found"));

        interactionMapper.updateInteractionFromDto(newInteraction, interaction);
        Interaction updatedInteraction = interactionRepository.save(interaction);

        List<Note> notes = noteRepository.findByNoteableTypeAndNoteableId("Interaction", updatedInteraction.getId());
        interaction.setNotes(notes);

        return interactionMapper.toDto(updatedInteraction);
    }

    public InteractionDto patchInteraction(Long id, InteractionPatchDto patchedInteraction) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Interaction " + id + " not found"));

        if (allFieldsNull(patchedInteraction)) {
            throw new BadRequestException("No valid fields provided for patch");
        }

        interactionMapper.patchInteractionFromDto(patchedInteraction, interaction);
        Interaction updatedInteraction = interactionRepository.save(interaction);

        List<Note> notes = noteRepository.findByNoteableTypeAndNoteableId("Interaction", updatedInteraction.getId());
        interaction.setNotes(notes);

        return interactionMapper.toDto(updatedInteraction);
    }

    public void deleteInteraction(Long id) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Interaction " + id + " not found"));

        interactionRepository.deleteById(id);
    }
}
