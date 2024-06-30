package com.aloc.aloc.color;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Color {

	@Id
	private String id;

	@Column(nullable = false)
	private String color1;

	private String color2;
	private String color3;
	private String color4;
	private String color5;

	@Column(nullable = false)
	private String category;

	private Integer degree;

}
