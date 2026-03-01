package com.TUKrefit.refit.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email", unique = true)
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId; // uuid string

    @Column(name = "email", nullable = false, length = 190)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Column(name = "created_at_ms", nullable = false)
    private Long createdAtMs;

    @Column(name = "updated_at_ms", nullable = false)
    private Long updatedAtMs;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private UserProfile profile;
}
