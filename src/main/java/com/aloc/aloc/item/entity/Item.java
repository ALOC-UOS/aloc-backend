package com.aloc.aloc.item.entity;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.item.enums.ItemLocation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item extends AuditingTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  private String description;

  @Column(nullable = false)
  private Integer coin;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ItemLocation itemLocation;

  @Column(nullable = false)
  private Boolean isHidden;
}
