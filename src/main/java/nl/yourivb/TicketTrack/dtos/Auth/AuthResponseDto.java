package nl.yourivb.TicketTrack.dtos.Auth;

import java.util.Date;

public class AuthResponseDto {
    private String jwt;
    private Date expiresAt;

    public AuthResponseDto(String jwt, Date expiresIn) {
        this.jwt = jwt;
        this.expiresAt = expiresIn;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
