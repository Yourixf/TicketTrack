package nl.yourivb.TicketTrack.controllers;

import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.models.AppUser;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AppUserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                // Status en content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Response envelope
                .andExpect(jsonPath("$.message", not(emptyOrNullString())))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.error", anyOf(nullValue(), emptyString())))
                .andExpect(jsonPath("$.timestamp", not(emptyOrNullString())))

                // Data array
                .andExpect(jsonPath("$.data", not(empty())))
                .andExpect(jsonPath("$.data.length()", greaterThan(0)))

                // Validation of 1 array item, the first
                .andExpect(jsonPath("$.data[0].id", greaterThan(0)))
                .andExpect(jsonPath("$.data[0].name", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data[0].phoneNumber", greaterThan(0)))
                .andExpect(jsonPath("$.data[0].email", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data[0]", hasKey("info")))
                .andExpect(jsonPath("$.data[0].created", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data[0].lastModified", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data[0]", hasKey("profilePictureId")))
                .andExpect(jsonPath("$.data[0].roleId", greaterThan(0)));
    }

    @Test
    void getUserById() throws Exception {
        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                // Status en content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Response envelope
                .andExpect(jsonPath("$.message", not(emptyOrNullString())))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.error", anyOf(nullValue(), emptyString())))
                .andExpect(jsonPath("$.timestamp", not(emptyOrNullString())))

                // Data array
                .andExpect(jsonPath("$.data", not(empty())))
                .andExpect(jsonPath("$.data.length()", greaterThan(1)))

                // Validation of 1 array item, the first
                .andExpect(jsonPath("$.data.id", greaterThan(0)))
                .andExpect(jsonPath("$.data.name", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data.phoneNumber", greaterThan(0)))
                .andExpect(jsonPath("$.data.email", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data", hasKey("info")))
                .andExpect(jsonPath("$.data.created", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data.lastModified", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data", hasKey("profilePictureId")))
                .andExpect(jsonPath("$.data.roleId", greaterThan(0)));
    }

    @Test
    void createUser() throws Exception {
        String email = "brucewayne@tickettrack.com";
        String rawPassword = "12345678";

        String requestJson = """
        {
          "name": "Bruce Wayne",
          "email": "%s",
          "password": "%s",
          "roleId": 3
        }
        """.formatted(email, rawPassword);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // status + headers
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/users/")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // envelope fields
                .andExpect(jsonPath("$.message").value("Created user"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.error", anyOf(nullValue(), emptyString())))
                .andExpect(jsonPath("$.timestamp", not(emptyOrNullString())))

                // nested data.* assertions
                .andExpect(jsonPath("$.data.id", greaterThan(0)))
                .andExpect(jsonPath("$.data.name").value("Bruce Wayne"))
                .andExpect(jsonPath("$.data.email").value("brucewayne@tickettrack.com"))
                .andExpect(jsonPath("$.data.roleId").value(3));

        // Check password is encrypted in DB
        AppUser savedUser = appUserRepository.findByEmail(email).orElseThrow(RecordNotFoundException::new);
        assertNotEquals(rawPassword, savedUser.getPassword()); // NOT plain text
        assertTrue(new BCryptPasswordEncoder().matches(rawPassword, savedUser.getPassword())); // ENCRYPTED match
    }

    @Test
    void patchUser() throws Exception {
        String newEmail = "john-wick@tickettrack.com";

        String requestJson = """
        {
          "email": "%s"
        }
        """.formatted(newEmail);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // Status en content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Response envelope
                .andExpect(jsonPath("$.message", not(emptyOrNullString())))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.error", anyOf(nullValue(), emptyString())))
                .andExpect(jsonPath("$.timestamp", not(emptyOrNullString())))

                // Data array
                .andExpect(jsonPath("$.data", not(empty())))
                .andExpect(jsonPath("$.data.length()", greaterThan(1)))

                // Validation of 1 array item, the first
                .andExpect(jsonPath("$.data.id", greaterThan(0)))
                .andExpect(jsonPath("$.data.name", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data.phoneNumber", greaterThan(0)))
                .andExpect(jsonPath("$.data.email", equalTo(newEmail)))
                .andExpect(jsonPath("$.data", hasKey("info")))
                .andExpect(jsonPath("$.data.created", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data.lastModified", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data", hasKey("profilePictureId")))
                .andExpect(jsonPath("$.data.roleId", greaterThan(0)));
    }
}