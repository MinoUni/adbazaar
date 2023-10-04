package com.adbazaar.model;

import com.adbazaar.dto.authentication.RegistrationRequest;
import com.adbazaar.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = {"books", "comments"})
@Entity
@Table(name = "users")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(name = "birt_date", columnDefinition = "DATE")
    private LocalDate dateOfBirth;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;

    @Builder.Default
    @Column(name = "verified")
    private Boolean isVerified = Boolean.FALSE;

    @Builder.Default
    @Column(name = "creation_date", columnDefinition = "DATE")
    private LocalDate creationDate = LocalDate.now();

    private String password;

    @Builder.Default
    @OneToMany(mappedBy = "seller", fetch = FetchType.EAGER)
    private List<Book> books = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    public static AppUser build(RegistrationRequest userDetails) {
        return AppUser.builder()
                .fullName(userDetails.getFullName())
                .email(userDetails.getEmail())
                .password(userDetails.getPassword())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
