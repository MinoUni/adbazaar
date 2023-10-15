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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = {"books", "comments", "favoriteBooks"})
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

    @Column(name = "image_url")
    private String imageUrl;

    private String phone;

    @Column(name = "birth_date", columnDefinition = "DATE")
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

    @Builder.Default
    @OneToMany
    @JoinTable(name = "book_favorite",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Book> favoriteBooks = new HashSet<>();

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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AppUser appUser = (AppUser) object;
        return Objects.equals(id, appUser.id) && Objects.equals(email, appUser.email) &&
                Objects.equals(phone, appUser.phone) && Objects.equals(creationDate, appUser.creationDate) &&
                Objects.equals(password, appUser.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, phone, creationDate, password);
    }
}
