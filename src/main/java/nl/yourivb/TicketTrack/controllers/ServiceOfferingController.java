package nl.yourivb.TicketTrack.controllers;

import jakarta.validation.Valid;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionInputDto;
import nl.yourivb.TicketTrack.dtos.interaction.InteractionPatchDto;
import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingDto;
import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingInputDto;
import nl.yourivb.TicketTrack.dtos.serviceOffering.ServiceOfferingPatchDto;
import nl.yourivb.TicketTrack.models.ServiceOffering;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.ServiceOfferingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class ServiceOfferingController {

    private final ServiceOfferingService serviceOfferingService;

    public ServiceOfferingController(ServiceOfferingService serviceOfferingService){
        this.serviceOfferingService = serviceOfferingService;
    }

    @GetMapping("/service-offerings")
    public ResponseEntity<ApiResponse<List<ServiceOfferingDto>>> getAllServiceOfferings() {
        List<ServiceOfferingDto> dtos;

        dtos = serviceOfferingService.getAllServiceOfferings();

        return new ResponseEntity<>(
                new ApiResponse<>("Service offerings fetched", HttpStatus.OK, dtos),
                HttpStatus.OK
        );
    }

    @GetMapping("/service-offerings/{id}")
    public ResponseEntity<ApiResponse<ServiceOfferingDto>> getServiceOfferingById(@PathVariable Long id){
        ServiceOfferingDto serviceOffering = serviceOfferingService.getServiceOfferingById(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched service offering " + id, HttpStatus.OK, serviceOffering),
                HttpStatus.OK
        );
    }

    @PostMapping("/service-offerings")
    public ResponseEntity<ApiResponse<ServiceOfferingDto>> addServiceOffering(@Valid @RequestBody ServiceOfferingInputDto serviceOfferingInputDto) {
        ServiceOfferingDto dto = serviceOfferingService.addServiceOffering(serviceOfferingInputDto);
        URI uri = URI.create("/service-offerings/" + dto.getId());

        return ResponseEntity.created(uri).body(new ApiResponse<>("Created service offering", HttpStatus.CREATED, dto));
    }

    @PutMapping("/service-offerings/{id}")
    public ResponseEntity<ApiResponse<ServiceOfferingDto>> updateServiceOffering(@PathVariable Long id, @Valid @RequestBody ServiceOfferingInputDto newServiceOffering) {
        ServiceOfferingDto updatedServiceOffering = serviceOfferingService.updateServiceOffering(id, newServiceOffering);

        return new ResponseEntity<>(
                new ApiResponse<>("Service offering updated", HttpStatus.OK, updatedServiceOffering), HttpStatus.OK
        );
    }

    @PatchMapping("/service-offerings/{id}")
    public ResponseEntity<ApiResponse<ServiceOfferingDto>> patchServiceOffering(@PathVariable Long id, @Valid @RequestBody ServiceOfferingPatchDto patchedServiceOffering) {
        ServiceOfferingDto updatedServiceOffering = serviceOfferingService.patchServiceOffering(id, patchedServiceOffering);

        return new ResponseEntity<>(
                new ApiResponse<>("Service offering updated", HttpStatus.OK, updatedServiceOffering), HttpStatus.OK
        );
    }

    @DeleteMapping("/service-offerings/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteServiceOffering(@PathVariable Long id) {
        serviceOfferingService.deleteServiceOffering(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Service offering deleted", HttpStatus.OK, null),
                HttpStatus.OK
        );
    }
}
