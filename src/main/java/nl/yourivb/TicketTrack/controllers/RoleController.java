package nl.yourivb.TicketTrack.controllers;

import nl.yourivb.TicketTrack.dtos.role.RoleDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAllRoles() {
        List<RoleDto> dtos = roleService.getAllRoles();

        return new ResponseEntity<> (
            new ApiResponse<>("Roles fetched", HttpStatus.OK, dtos),
            HttpStatus.OK
        );
    } 

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> getRoleById(@PathVariable Long id) {
        RoleDto role = roleService.getRoleById(id);

         return new ResponseEntity<>(
                new ApiResponse<>("Fetched role " + id, HttpStatus.OK, role),
                HttpStatus.OK
        );
    }
}