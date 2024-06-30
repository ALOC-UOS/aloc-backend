package com.aloc.aloc.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.UserResponseDto;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByGithubId(String githubId);

	Optional<User> findByRefreshToken(String refreshToken);

	Optional<User> findByUsername(String username);

	List<UserResponseDto> findAllByAuthority(String authority);

	boolean existsByGithubId(String username);

	boolean existsByBaekjoonId(String username);
}
