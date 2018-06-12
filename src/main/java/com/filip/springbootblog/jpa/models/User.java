package com.filip.springbootblog.jpa.models;

import com.filip.springbootblog.jpa.enums.Role;
import com.filip.springbootblog.jpa.enums.SignInProvider;
import com.filip.springbootblog.jpa.models.validators.ExtendedEmailValidator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;

import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MAX_LENGTH_EMAIL_ADDRESS;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MAX_LENGTH_FIRST_NAME;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MAX_LENGTH_LAST_NAME;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MAX_LENGTH_USERNAME;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MIN_LENGTH_PASSWORD;
import static com.filip.springbootblog.jpa.constants.ValidationForEntities.MIN_LENGTH_USERNAME;

@Slf4j
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails, Serializable {

    private static final long serialVersionUID = 2002390446280945447L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    protected Long id;

    @Transient
    public boolean isNew() {
        return (this.id == null);
    }

    @Column(unique = true)
    @NotEmpty
    @Length(min = MIN_LENGTH_USERNAME, max = MAX_LENGTH_USERNAME)
    private String username;

    @Column
    @NotEmpty
    @Length(min = MIN_LENGTH_PASSWORD)
    private String password;

    @Basic
    @ExtendedEmailValidator
    @NotEmpty
    @Length(max = MAX_LENGTH_EMAIL_ADDRESS)
    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name")
    @NotEmpty
    @Length(max = MAX_LENGTH_FIRST_NAME)
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty
    @Length(max = MAX_LENGTH_LAST_NAME)
    private String lastName;

    @Column(name = "account_expired")
    private boolean accountExpired = false;

    @Column(name = "account_locked")
    private boolean accountLocked = false;

    @Column(name = "credentials_expired")
    private boolean credentialsExpired = false;

    @Column(name = "provider_id", length = 25)
    @Enumerated(EnumType.STRING)
    private SignInProvider signInProvider;

    @Column(name = "user_key", length = 25)
    private String userKey;

    @Column(name = "has_avatar")
    private boolean hasAvatar = false;

    @Column
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    public Collection<Authority> authorities;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserProfile userProfile;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    public UserData userData;

    public User() {
        this.authorities = new LinkedHashSet<>();
    }

    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public boolean hasAuthority(Role role) {
        return hasAuthority(String.valueOf(role));
    }

    private boolean hasAuthority(String targetAuthority) {
        if (targetAuthority == null) {
            return false;
        }
        if (authorities == null) {
            log.debug("authorities is null for user " + this);
        }

        return authorities.stream().anyMatch(authority -> targetAuthority.equals(authority.getAuthority()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username=" + username +
                ", email=" + email +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", accountExpired=" + accountExpired +
                ", accountLocked=" + accountLocked +
                ", credentialsExpired=" + credentialsExpired +
                ", userKey=" + userKey +
                ", hasAvatar=" + hasAvatar +
                ", signInProvider=" + signInProvider +
                ", enabled=" + enabled +
                ", new=" + this.isNew() +
                '}';
    }

    public void update(String username, String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
    }

    public void update(UserData userData) {
        this.userData = userData;
    }
}
