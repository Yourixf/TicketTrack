package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.InteractionDto;
import nl.yourivb.TicketTrack.dtos.InteractionInputDto;
import nl.yourivb.TicketTrack.mappers.InteractionMapper;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
        List<Interaction> interactionList = interactionRepository.findAll();
        List<InteractionDto> interactionDtoList = new ArrayList<>();

        for (Interaction interaction : interactionList) {
            InteractionDto dto = interactionMapper.toDto(interaction);
            interactionDtoList.add(dto);
        }

        return interactionDtoList;
    }

    public InteractionDto addInteraction(InteractionInputDto dto) {
        Interaction interaction = interactionMapper.toModel(dto);
        interaction.setNumber(generateInteractionNumber());
        interaction.setCreated(LocalDateTime.now());
        interactionRepository.save(interaction);

        return interactionMapper.toDto(interaction);
    }

    public String generateInteractionNumber() {
        Long count = interactionRepository.count();
        Long nextnumber = count + 1;
        return String.format("IMS%07d" , nextnumber);
    }
}
