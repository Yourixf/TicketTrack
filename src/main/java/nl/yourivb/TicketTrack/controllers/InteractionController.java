package nl.yourivb.TicketTrack.controllers;

import jakarta.validation.Valid;
import nl.yourivb.TicketTrack.dtos.IdInputDto;
import nl.yourivb.TicketTrack.dtos.InteractionDto;
import nl.yourivb.TicketTrack.dtos.InteractionInputDto;
import nl.yourivb.TicketTrack.models.Interaction;
import nl.yourivb.TicketTrack.services.InteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }


    @GetMapping("/interactions")
    public ResponseEntity<List<InteractionDto>> getAllInteractions() {
        List<InteractionDto> dtos;

        dtos = interactionService.getAllInteractions();

        return ResponseEntity.ok().body(dtos);
    }


    @GetMapping("/interactions/{id}")
    public ResponseEntity<InteractionDto> getInteractionById(@PathVariable Long id){
        InteractionDto interaction = interactionService.getInteractionById(id);

        return ResponseEntity.ok().body(interaction);
    }


    @PostMapping("/interactions")
    public ResponseEntity<InteractionDto> addInteraction(@Valid @RequestBody InteractionInputDto interactionInputDto) {
        InteractionDto dto = interactionService.addInteraction(interactionInputDto);
        URI uri = URI.create("/interactions/" + dto.getId());
        return ResponseEntity.created(uri).body(dto);
    }

//    @PutMapping("/interactions/{id}")
//
//    @PatchMapping("/interactions/{id}")
//
//    @DeleteMapping("/interactions/{id}")
}
