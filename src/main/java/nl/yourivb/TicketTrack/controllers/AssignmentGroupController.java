package nl.yourivb.TicketTrack.controllers;

import jakarta.validation.Valid;
import nl.yourivb.TicketTrack.dtos.assignmentgroup.AssignmentGroupDto;
import nl.yourivb.TicketTrack.dtos.assignmentgroup.AssignmentGroupInputDto;
import nl.yourivb.TicketTrack.dtos.assignmentgroup.AssignmentGroupPatchDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.AssignmentGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/assignment-groups")
public class AssignmentGroupController {

    private final AssignmentGroupService assignmentGroupService;

    public AssignmentGroupController(AssignmentGroupService assignmentGroupService){
        this.assignmentGroupService = assignmentGroupService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssignmentGroupDto>>> getAllAssignmentGroups() {
        List<AssignmentGroupDto> dtos = assignmentGroupService.getAllAssignmentGroups();

        return new ResponseEntity<>(
                new ApiResponse<>("Assignment groups fetched", HttpStatus.OK, dtos),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentGroupDto>> getAssignmentGroupById(@PathVariable Long id){
        AssignmentGroupDto assignmentGroup = assignmentGroupService.getAssignmentGroupById(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched assignment group " + id, HttpStatus.OK, assignmentGroup),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AssignmentGroupDto>> addAssignmentGroup(@Valid @RequestBody AssignmentGroupInputDto assignmentGroupInputDto) {
        AssignmentGroupDto dto = assignmentGroupService.addAssignmentGroup(assignmentGroupInputDto);
        URI uri = URI.create("/assignment-groups/" + dto.getId());

        return ResponseEntity.created(uri).body(new ApiResponse<>("Created assignment group", HttpStatus.CREATED, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentGroupDto>> updateAssignmentGroup(@PathVariable Long id, @Valid @RequestBody AssignmentGroupInputDto newAssignmentGroup) {
        AssignmentGroupDto updatedAssignmentGroup = assignmentGroupService.updateAssignmentGroup(id, newAssignmentGroup);

        return new ResponseEntity<>(
                new ApiResponse<>("Assignment group updated", HttpStatus.OK, updatedAssignmentGroup),
                HttpStatus.OK
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentGroupDto>> patchAssignmentGroup(@PathVariable Long id, @Valid @RequestBody AssignmentGroupPatchDto patchedAssignmentGroup) {
        AssignmentGroupDto updatedAssignmentGroup = assignmentGroupService.patchAssignmentGroup(id, patchedAssignmentGroup);

        return new ResponseEntity<>(
                new ApiResponse<>("Assignment group updated", HttpStatus.OK, updatedAssignmentGroup),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssignmentGroup(@PathVariable Long id) {
        assignmentGroupService.deleteAssignmentGroup(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Assignment group deleted", HttpStatus.OK, null),
                HttpStatus.OK
        );
    }
}
