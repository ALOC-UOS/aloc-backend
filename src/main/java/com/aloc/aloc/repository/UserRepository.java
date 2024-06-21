package com.aloc.aloc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByGithubId(String email);
}
