package com.aloc.aloc.algorithm.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmPrimaryKey implements Serializable {
	private Integer algorithmId;
	private Integer season;
}
