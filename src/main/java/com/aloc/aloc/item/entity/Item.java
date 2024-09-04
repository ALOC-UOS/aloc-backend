package com.aloc.aloc.item.entity;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.item.enums.ItemType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
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
	private ItemType itemType;
}
