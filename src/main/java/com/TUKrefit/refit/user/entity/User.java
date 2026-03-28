package com.TUKrefit.refit.user.entity;

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
    private String userId; // users PK(uuid 문자열)

    @Column(name = "email", nullable = false, length = 190)
    private String email; // 로그인 ID

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash; // BCrypt 해시만 저장

    @Column(name = "name", nullable = false, length = 50)
    private String name; // 표시 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role; // PATIENT/THERAPIST/ADMIN

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender; // M/F/OTHER(선택)

    @Column(name = "birth_year")
    private Integer birthYear; // 연령대 분석용 출생년도

    @Column(name = "created_at_ms", nullable = false)
    private Long createdAtMs; // 생성 시각(epoch ms)

    @Column(name = "updated_at_ms", nullable = false)
    private Long updatedAtMs; // 수정 시각(epoch ms)

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private UserProfile profile; // user_profile 1:1 연결
}
