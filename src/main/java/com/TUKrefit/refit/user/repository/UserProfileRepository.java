package com.TUKrefit.refit.user.repository;

import com.TUKrefit.refit.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
}
