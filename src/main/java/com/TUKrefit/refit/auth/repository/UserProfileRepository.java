package com.TUKrefit.refit.auth.repository;

import com.TUKrefit.refit.auth.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
}
