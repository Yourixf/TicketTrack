package nl.yourivb.TicketTrack.dtos.appuser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class AppUserPatchDto {
    @Size(min = 2, max = 255)
    private String name;

    private Long phoneNumber;

    @Email
    @Size(min = 2, max = 255)
    private String email;

    @Size(min = 2, max = 500)
    private String info;
    private String password;
    private Long profilePictureId;
    private Long roleId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(Long profilePictureId) {
        this.profilePictureId = profilePictureId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
