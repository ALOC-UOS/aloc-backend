package com.aloc.aloc.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.domain.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByGithubId(String githubId);

	Optional<User> findByRefreshToken(String refreshToken);

	Optional<User> findByUsername(String username);

	boolean existsByGithubId(String username);

	boolean existsByBaekjoonId(String username);
}
