package nl.yourivb.TicketTrack.dtos.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoleInputDto {
        @NotBlank
        @Size(min = 2, max = 255)
        private String name;

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }
}