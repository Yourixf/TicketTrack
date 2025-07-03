package nl.yourivb.TicketTrack.controllers;

import jakarta.validation.Valid;
import nl.yourivb.TicketTrack.dtos.InteractionDto;
import nl.yourivb.TicketTrack.dtos.InteractionInputDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.InteractionService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<List<InteractionDto>>>  getAllInteractions() {
        List<InteractionDto> dtos;

        dtos = interactionService.getAllInteractions();

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched interactions", HttpStatus.OK, dtos),
                HttpStatus.OK
        );
    }

    @GetMapping("/interactions/{id}")
    public ResponseEntity<ApiResponse<InteractionDto>> getInteractionById(@PathVariable Long id){
        InteractionDto interaction = interactionService.getInteractionById(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched interaction " + id, HttpStatus.OK, interaction),
                HttpStatus.OK
        );
    }


    @PostMapping("/interactions")
    public ResponseEntity<ApiResponse<InteractionDto>> addInteraction(@Valid @RequestBody InteractionInputDto interactionInputDto) {
        InteractionDto dto = interactionService.addInteraction(interactionInputDto);
        URI uri = URI.create("/interactions/" + dto.getId());

        return new ResponseEntity<>(
                new ApiResponse<>("Created interaction ", HttpStatus.CREATED, dto),
                HttpStatus.CREATED
        );

    }

//    @PutMapping("/interactions/{id}")
//
//    @PatchMapping("/interactions/{id}")
//
//    @DeleteMapping("/interactions/{id}")
}
