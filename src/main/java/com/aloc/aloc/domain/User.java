package com.aloc.aloc.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.persistence.Column;

@Entity
@Getter
@Setter
@Table(name="study_user")
public class User {

    @Id
    @Column(length=36)
    private String id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String baekjoonId;

    @Column(nullable = false)
    private String githubId;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private Integer profileNumber;

    // TODO: 삭제 예정
    @Column(nullable = false)
    private LocalDateTime joinedAt;

    private Integer rank;

    @Column(nullable = false)
    private Integer coin = 0;

    @Column(nullable = false)
    private String profileColor = "default";

    @Column(nullable = false)
    private String password;
}
