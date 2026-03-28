package com.TUKrefit.refit.user.repository;

import com.TUKrefit.refit.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // 이메일 정규화(lowercase) 기준 조회
    Optional<User> findByEmail(String email);
    // 회원가입 중복 검사
    boolean existsByEmail(String email);
}
