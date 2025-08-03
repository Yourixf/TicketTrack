package nl.yourivb.TicketTrack.controllers;

import jakarta.validation.Valid;
import nl.yourivb.TicketTrack.services.RoleService;
import org.springframework.http.HttpStatus;

import nl.yourivb.TicketTrack.dtos.Role.RoleInputDto;
import nl.yourivb.TicketTrack.dtos.Role.RoleDto;
import nl.yourivb.TicketTrack.models.Role;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles") 
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAllRoles() {
        List<RoleDto> dtos = roleService.getAllRoles();

        return new ResponseEntity<> (
            new ApiResponse<>("Roles fetched", HttpStatus.OK, dtos),
            HttpStatus.OK
        );
    } 

    @GetMapping("/roles/{id}") 
    public ResponseEntity<ApiResponse<RoleDto>> getRoleById(@PathVariable Long id) {
        RoleDto role = roleService.getRoleById(id);

         return new ResponseEntity<>(
                new ApiResponse<>("Fetched role " + id, HttpStatus.OK, role),
                HttpStatus.OK
        );
    }

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<RoleDto>> addRole(@Valid @RequestBody RoleInputDto dto) {
        RoleDto role = roleService.addRole(dto);
        URI uri = URI.create("/roles/" + role.getId());

        return ResponseEntity.created(uri).body(
                new ApiResponse<>("Created role", HttpStatus.CREATED, role)
        );
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> updateRole(@PathVariable Long id, @Valid @RequestBody RoleInputDto dto) {
        RoleDto role = roleService.updateRole(id, dto);

        return new ResponseEntity<>(
                new ApiResponse<>("Role updated", HttpStatus.OK, role),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);

         return new ResponseEntity<>(
                new ApiResponse<>("Role deleted", HttpStatus.OK, null),
                HttpStatus.OK
        );
    }

}