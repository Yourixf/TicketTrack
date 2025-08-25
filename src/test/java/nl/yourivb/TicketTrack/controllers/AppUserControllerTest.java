package nl.yourivb.TicketTrack.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// static imports
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AppUserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllUsers() throws Exception{

        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createUser() throws Exception {
        String requestJson = """
                {
                  "name": "Bruce Wayne",
                  "email": "brucewayne@tickettrack.com",
                  "password": "12345678",
                  "roleId": 3
                }
                """;

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
    }
}