package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.IdInputDto;
import nl.yourivb.TicketTrack.dtos.InteractionDto;
import nl.yourivb.TicketTrack.dtos.InteractionInputDto;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.mappers.InteractionMapper;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.models.AssignmentGroup;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.models.ServiceOffering;
import nl.yourivb.TicketTrack.repositories.InteractionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class InteractionService {
    private final InteractionRepository interactionRepository;
    private final InteractionMapper interactionMapper;
    private final EntityLookupService entityLookupService;

    public InteractionService(InteractionRepository interactionRepository,
                              InteractionMapper interactionMapper, EntityLookupService entityLookupService) {
        this.interactionRepository = interactionRepository;
        this.interactionMapper = interactionMapper;
        this.entityLookupService = entityLookupService;
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

        /*AppUser openedFor = entityLookupService.getAppUserById(dto.getOpenedForId());
        AssignmentGroup assignmentGroup = entityLookupService.getAssignmentGroupById(dto.getAssignmentGroupId());
        ServiceOffering serviceOffering = entityLookupService.getServiceOfferingById(dto.getServiceOfferingId());
*/


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
