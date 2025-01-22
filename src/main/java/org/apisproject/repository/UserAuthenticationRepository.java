package org.apisproject.repository;

import org.apisproject.entity.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, Long> {
    Optional<UserAuthentication> findByUserId(Long userId);

    UserAuthentication findByUsername(String username);
}
