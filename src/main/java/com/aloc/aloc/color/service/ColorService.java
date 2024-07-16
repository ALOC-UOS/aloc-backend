package com.aloc.aloc.color.service;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.dto.response.ColorResponseDto;
import com.aloc.aloc.color.repository.ColorRepository;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ColorService {
	private final ColorRepository colorRepository;
	private final UserRepository userRepository;

	public Color getColorById(String id) {
		return colorRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 컬러가 없습니다. " + id));
	}

	public String pickColor() {
		Random random = new Random();
		int draw = random.nextInt(100) + 1;

		List<Color> colorList;
		if (draw <= 85) {
			colorList = colorRepository.findByCategory("common");
		} else if (draw <= 95) {
			colorList = colorRepository.findByCategory("rare");
		} else {
			colorList = colorRepository.findByCategory("special");
		}

		return colorList.get(random.nextInt(colorList.size())).getId();
	}

	public ColorResponseDto changeColor(String githubId) {
		User user = userRepository.findByGithubId(githubId).orElseThrow();
		if (user.getCoin() < 100) {
			throw new IllegalArgumentException("코인이 부족합니다.");
		}
		user.setCoin(user.getCoin() - 100);

		String colorName = pickColor();
		user.setProfileColor(colorName);
		Color color = colorRepository.findById(colorName).orElseThrow();

		userRepository.save(user);
		return new ColorResponseDto(user.getCoin(), color.getId(), color.getColor1());
	}
}
