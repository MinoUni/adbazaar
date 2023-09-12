package com.adbazaar.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @Column(name = "user_id")
    private Long id;

    private String token;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    public void revoke() {
        user.setToken(null);
        setUser(null);
    }
}
