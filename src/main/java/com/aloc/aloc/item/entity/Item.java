package com.aloc.aloc.item.entity;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.item.enums.ItemType;
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
	private String name;
	private Integer coin;
	@Enumerated(EnumType.STRING)
	private ItemType itemType;
}
