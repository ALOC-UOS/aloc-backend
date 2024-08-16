package com.aloc.aloc.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByGithubId(String githubId);

	Optional<User> findByRefreshToken(String refreshToken);

	List<User> findAllByAuthorityIn(Set<Authority> authorities);

	boolean existsByGithubId(String username);

	boolean existsByBaekjoonId(String username);

	void deleteByGithubId(String githubId);

	List<User> findAllByAuthorityInAndCourse(Set<Authority> activeAuthorities, Course course);
}
