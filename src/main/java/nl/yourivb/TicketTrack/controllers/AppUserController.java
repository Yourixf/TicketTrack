package nl.yourivb.TicketTrack.controllers;

import jakarta.validation.Valid;
import nl.yourivb.TicketTrack.dtos.appuser.AppUserDto;
import nl.yourivb.TicketTrack.dtos.appuser.AppUserInputDto;
import nl.yourivb.TicketTrack.dtos.appuser.AppUserPatchDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AppUserDto>>> getAllUsers() {
        List<AppUserDto> dtos = appUserService.getAllUsers();

        return new ResponseEntity<>(
                new ApiResponse<>("Users fetched", HttpStatus.OK, dtos),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppUserDto>> getUserById(@PathVariable Long id) {
        AppUserDto dto = appUserService.getUserById(id);

        return new ResponseEntity<>(
                new ApiResponse<>("Fetched user " + id, HttpStatus.OK, dto),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppUserDto>> createUser(@Valid @RequestBody AppUserInputDto dto) {
        AppUserDto created = appUserService.createUser(dto);
        URI uri = URI.create("/users/" + created.getId());

        return ResponseEntity.created(uri).body(
                new ApiResponse<>("Created user", HttpStatus.CREATED, created)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppUserDto>> updateUser(@PathVariable Long id, @Valid @RequestBody AppUserInputDto dto) {
        AppUserDto updated = appUserService.updateUser(id, dto);

        return new ResponseEntity<>(
                new ApiResponse<>("User updated", HttpStatus.OK, updated),
                HttpStatus.OK
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AppUserDto>> patchUser(@PathVariable Long id, @Valid @RequestBody AppUserPatchDto dto) {
        AppUserDto updated = appUserService.patchUser(id, dto);

        return new ResponseEntity<>(
                new ApiResponse<>("User updated", HttpStatus.OK, updated),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        appUserService.deleteUser(id);

        return new ResponseEntity<>(
                new ApiResponse<>("User deleted", HttpStatus.OK, null),
                HttpStatus.OK
        );
    }
}
