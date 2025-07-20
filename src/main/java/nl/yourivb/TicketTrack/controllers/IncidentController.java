package nl.yourivb.TicketTrack.controllers;

import nl.yourivb.TicketTrack.dtos.Incident.IncidentDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.IncidentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping("/incidents")
    public ResponseEntity<ApiResponse<List<IncidentDto>>> getAllIncidents() {
        List<IncidentDto> dtos;

        dtos = incidentService.getAllIncidents();

        return new ResponseEntity<>(
                new ApiResponse<>("Incidents fetched", HttpStatus.OK, dtos), HttpStatus.OK
        );
    }

    @GetMapping("/incidents/{id}")
    public ResponseEntity<ApiResponse<IncidentDto>> getIncidentById(@PathVariable Long id) {
        IncidentDto incident = incidentService.getIncidentById(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched incident " + id, HttpStatus.OK, incident), HttpStatus.OK
        );
    }
}
