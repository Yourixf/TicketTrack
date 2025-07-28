package nl.yourivb.TicketTrack.controllers;

import nl.yourivb.TicketTrack.dtos.AppUser.AppUserDto;
import nl.yourivb.TicketTrack.dtos.AppUser.AppUserInputDto;
import nl.yourivb.TicketTrack.dtos.AppUser.AppUserPatchDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService service) {
        this.appUserService = service;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AppUserDto>>> getAllUsers() {
        return ResponseEntity.ok(new ApiResponse<>("User list", HttpStatus.OK, appUserService.getAllUsers()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<AppUserDto>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("User details", HttpStatus.OK, appUserService.getUserById(id)));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<AppUserDto>> createUser(@RequestBody AppUserInputDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("User created", HttpStatus.CREATED, appUserService.createUser(dto)));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<AppUserDto>> updateUser(@PathVariable Long id, @RequestBody AppUserInputDto dto) {
        return ResponseEntity.ok(new ApiResponse<>("User updated", HttpStatus.OK, appUserService.updateUser(id, dto)));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<ApiResponse<AppUserDto>> patchUser(@PathVariable Long id, @RequestBody AppUserPatchDto dto) {
        return ResponseEntity.ok(new ApiResponse<>("User patched", HttpStatus.OK, appUserService.patchUser(id, dto)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        appUserService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("User deleted", HttpStatus.OK, null));
    }
}
