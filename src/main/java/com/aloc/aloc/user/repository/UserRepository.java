package com.aloc.aloc.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.UserResponseDto;
import com.aloc.aloc.user.enums.Authority;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByGithubId(String githubId);

	Optional<User> findByRefreshToken(String refreshToken);

	List<User> findAllByAuthorityIn(List<Authority> authorities);

	boolean existsByGithubId(String username);

	boolean existsByBaekjoonId(String username);

	void deleteByGithubId(String githubId);
}
