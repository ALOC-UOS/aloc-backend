package com.aloc.aloc.user.service;

import org.springframework.stereotype.Component;

import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.service.ColorService;
import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
	private final ProblemFacade problemFacade;
	private final ColorService colorService;

	UserDetailResponseDto mapToUserDetailResponseDto(User user) {
		Integer problemCounts = problemFacade.getTotalProblemCountByCourse(user.getCourse());
		Color userColor = colorService.getColorById(user.getProfileColor());

		return UserDetailResponseDto.of(user,
			(problemCounts - user.getSolvedCount()),
			problemFacade.getTodayProblemSolved(user.getId(), user.getCourse()),
			userColor.getCategory(),
			userColor.getColor1(),
			userColor.getColor2(),
			userColor.getColor3(),
			userColor.getColor4(),
			userColor.getColor5(),
			userColor.getDegree()
			);
	}
}
