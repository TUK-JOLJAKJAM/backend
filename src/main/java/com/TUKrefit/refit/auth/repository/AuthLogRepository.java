package com.TUKrefit.refit.auth.repository;

import com.TUKrefit.refit.auth.entity.AuthLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthLogRepository extends JpaRepository<AuthLog, String> {
    Optional<AuthLog> findByAuthIdAndLogoutAtMsIsNull(String authId);
}