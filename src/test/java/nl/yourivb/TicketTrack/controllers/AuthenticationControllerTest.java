package nl.yourivb.TicketTrack.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAuthenticationToken() throws Exception {
        String email = "johnwick@tickettrack.com";
        String rawPassword = "12345678";

        String requestJson = """
        {
          "email": "%s",
          "password": "%s"
        }
        """.formatted(email, rawPassword);

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // status + headers
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // envelope fields
                .andExpect(jsonPath("$.message").value("Authentication successful"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.error", anyOf(nullValue(), emptyString())))
                .andExpect(jsonPath("$.timestamp", not(emptyOrNullString())))

                // nested data.* assertions
                .andExpect(jsonPath("$.data.jwt", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data.expiresAt", not(emptyOrNullString())));
    }

    @Test
    void createAuthenticationTokenWithFalseCredentials() throws Exception {
        String email = "johnwick@tickettrack.com";
        String rawPassword = "password";

        String requestJson = """
        {
          "email": "%s",
          "password": "%s"
        }
        """.formatted(email, rawPassword);

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // status + headers
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // envelope fields
                .andExpect(jsonPath("$.message").value("Authentication successful"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error", anyOf(nullValue(), emptyString())))
                .andExpect(jsonPath("$.timestamp", not(emptyOrNullString())))

                // nested data.* assertions
                .andExpect(jsonPath("$.data.jwt", not(emptyOrNullString())))
                .andExpect(jsonPath("$.data.expiresAt", not(emptyOrNullString())));
    }
}