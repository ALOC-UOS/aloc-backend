package com.aloc.aloc.tag.repository;

import com.aloc.aloc.tag.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

  Optional<Tag> findByKoreanNameAndEnglishName(String koreanName, String englishName);

  Optional<Tag> findByKoreanName(String tagName);
}
