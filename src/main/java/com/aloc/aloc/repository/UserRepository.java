package com.aloc.aloc.repository;

import com.aloc.aloc.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
