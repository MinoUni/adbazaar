package com.adbazaar.repository;

import com.adbazaar.dto.user.UserDetails;
import com.adbazaar.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT new com.adbazaar.dto.user.UserDetails(u.id, u.isVerified, u.fullName, u.email, u.phone, u.imageUrl, u.dateOfBirth)
            FROM AppUser u
            WHERE u.email = :email""")
    Optional<UserDetails> findUserDetailsByEmail(@Param("email") String email);
}
