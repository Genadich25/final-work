package ru.skypro.homework.entities;

import javax.persistence.*;

/**
 * Class contains entity of user
 */
@Entity
@Table(name = "users")
public class SiteUser {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @OneToOne(cascade = CascadeType.ALL)
    private Authority authority;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "site_user_details_id")
    private SiteUserDetails siteUserDetails;

    public SiteUser() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Authority getAuthority() {
        return authority;
    }

    public SiteUserDetails getSiteUserDetails() {
        return siteUserDetails;
    }

    public void setSiteUserDetails(SiteUserDetails siteUserDetails) {
        this.siteUserDetails = siteUserDetails;
    }
}
