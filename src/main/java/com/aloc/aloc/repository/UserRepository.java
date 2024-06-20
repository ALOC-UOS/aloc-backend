package com.aloc.aloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aloc.aloc.domain.User;


public interface UserRepository extends JpaRepository<User, String> {

}
