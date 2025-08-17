package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.interaction.InteractionDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionInputDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionPatchDto;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.InteractionMapper;
import nl.yourivb.TicketTrack.mappers.NoteMapper;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.models.enums.Category;
import nl.yourivb.TicketTrack.models.enums.Channel;
import nl.yourivb.TicketTrack.repositories.AttachmentRepository;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import nl.yourivb.TicketTrack.repositories.NoteRepository;
import nl.yourivb.TicketTrack.security.SecurityUtils;
import nl.yourivb.TicketTrack.utils.AppUtils;
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
    private final NoteMapper noteMapper;

    public InteractionService(InteractionRepository interactionRepository,
                              InteractionMapper interactionMapper,
                              NoteRepository noteRepository,
                              AttachmentRepository attachmentRepository, NoteMapper noteMapper) {
        this.interactionRepository = interactionRepository;
        this.interactionMapper = interactionMapper;
        this.noteRepository = noteRepository;
        this.attachmentRepository = attachmentRepository;
        this.noteMapper = noteMapper;
    }

    public List<InteractionDto> getAllInteractions() {
        // this finds alls interactions, loops through each, gets and sets the corresponding note & atta list.
        return interactionRepository.findAll()
                .stream()
                .map(interaction -> {
                    AppUtils.enrichWithRelations(
                            interaction,
                            "Interaction",
                            interaction.getId(),
                            noteRepository,
                            attachmentRepository
                    );

                    return interactionMapper.toDto(interaction);
                })
                .toList();
    }

    public InteractionDto getInteractionById(Long id) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Interaction " + id + " not found"));

        AppUtils.enrichWithRelations(
                interaction,
                "Interaction",
                id,
                noteRepository,
                attachmentRepository
        );

        return interactionMapper.toDto(interaction);
    }

    public InteractionDto addInteraction(InteractionInputDto dto) {
        Interaction interaction = interactionMapper.toModel(dto);

        interaction.setNumber(generateRegistrationNumber("IMS", interactionRepository));
        interaction.setOpenedBy(SecurityUtils.getCurrentUserDetails().getAppUser());

        if (SecurityUtils.hasRole("CUSTOMER")) {
            interaction.setChannel(Channel.SELF_SERVICE);
            interaction.setCategory(Category.USER_ASSISTANCE);

            if (dto.getOpenedForId() == interaction.getOpenedBy().getId()) {
                interaction.setOpenedFor(null); // prevents customers selecting "for someone else" and using their own name/id.
            }
        }

        interactionRepository.save(interaction);

        return interactionMapper.toDto(interaction);
    }

    public InteractionDto updateInteraction(Long id, InteractionInputDto newInteraction) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Interaction " + id + " not found"));

        interactionMapper.updateInteractionFromDto(newInteraction, interaction);
        interactionRepository.save(interaction);

        AppUtils.enrichWithRelations(
                interaction,
                "Interaction",
                id,
                noteRepository,
                attachmentRepository
        );

        return interactionMapper.toDto(interaction);
    }

    public InteractionDto patchInteraction(Long id, InteractionPatchDto patchedInteraction) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Interaction " + id + " not found"));

        if (allFieldsNull(patchedInteraction)) {
            throw new BadRequestException("No valid fields provided for patch");
        }

        interactionMapper.patchInteractionFromDto(patchedInteraction, interaction);
        interactionRepository.save(interaction);

        AppUtils.enrichWithRelations(
                interaction,
                "Interaction",
                id,
                noteRepository,
                attachmentRepository
        );

        return interactionMapper.toDto(interaction);
    }

    public void deleteInteraction(Long id) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Interaction " + id + " not found"));

        interactionRepository.deleteById(id);
    }
}
