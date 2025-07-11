package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.interaction.InteractionDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionInputDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionPatchDto;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.exceptions.BadRequestException;
import nl.yourivb.TicketTrack.mappers.InteractionMapper;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static nl.yourivb.TicketTrack.utils.AppUtils.allFieldsNull;
import static nl.yourivb.TicketTrack.utils.AppUtils.generateRegistrationNumber;


@Service
public class InteractionService {
    private final InteractionRepository interactionRepository;
    private final InteractionMapper interactionMapper;

    public InteractionService(InteractionRepository interactionRepository,
                              InteractionMapper interactionMapper) {
        this.interactionRepository = interactionRepository;
        this.interactionMapper = interactionMapper;
    }

    public List<InteractionDto> getAllInteractions() {
        return interactionRepository.findAll().stream().map(interactionMapper::toDto).toList();
    }

    public InteractionDto getInteractionById(Long id) {
        Optional<Interaction> interactionOptional = interactionRepository.findById(id);

        if (interactionOptional.isPresent()) {
            Interaction interaction = interactionOptional.get();
            return interactionMapper.toDto(interaction);
        } else {
            throw new RecordNotFoundException("Interaction " + id + " not found in the database");
        }
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

        return interactionMapper.toDto(updatedInteraction);
    }

    public InteractionDto patchInteraction(Long id, InteractionPatchDto patchedInteraction) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Interaction " + id + " not found"));

        if (allFieldsNull(patchedInteraction)) {
            throw new BadRequestException("No valid fields provided for patch");
        }

        interactionMapper.patchInteractionFromDto(patchedInteraction, interaction);
        Interaction updatedInteraction = interactionRepository.save(interaction);

        return interactionMapper.toDto(updatedInteraction);
    }

    public void deleteInteraction(Long id) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Interaction " + id + " not found"));

        interactionRepository.deleteById(id);
    }
}
