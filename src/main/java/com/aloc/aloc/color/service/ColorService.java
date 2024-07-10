package com.aloc.aloc.color.service;

import org.springframework.stereotype.Service;

import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.repository.ColorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ColorService {
	private final ColorRepository colorRepository;

	public Color getColorById(String id) {
		return colorRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 컬러가 없습니다. " + id));
	}
}
