package com.aloc.aloc.user.service;

import org.springframework.stereotype.Service;

import com.aloc.aloc.history.service.HistoryService;
import com.aloc.aloc.problem.service.UserProblemRecordLoader;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.enums.Authority;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
	private final UserService userService;
	private final HistoryService historyService;
	private final UserProblemRecordLoader userProblemRecordLoader;

	public String addUser(String username, String githubId) {
		userService.checkAdmin(username);
		User user = userService.findUser(githubId);

		if (Authority.ROLE_USER.equals(user.getAuthority())) {
			throw new IllegalArgumentException("이미 등록된 멤버입니다.");
		}
		user.setAuthority(Authority.ROLE_USER);
		userProblemRecordLoader.loadUserProblemRecord(user);
		historyService.addHistory(user, "plusMember", null);
		return "스터디 멤버로 등록되었습니다.";
	}

}
