package com.aloc.aloc.color.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.color.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, String> {
	Optional<Color> findById(String id);

	List<Color> findByCategory(String common);
}
