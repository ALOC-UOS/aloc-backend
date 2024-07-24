package com.aloc.aloc.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.alcorequest.AlocRequest;
import com.aloc.aloc.alcorequest.repository.AlocRequestRepository;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlocRequestScheduler {
	private final AlocRequestRepository alocRequestRepository;
	private final UserRepository userRepository;

	@Transactional
	@Scheduled(cron = "0 0 0 * * TUE") // 추후 UserProblem에 매주 문제 추가하는 스케쥴 구현 후, 이 함수 먼저 호출하고 그다음 문제 추가하기
	public void resolveCourseChangeRequest() {
		List<AlocRequest> requests = alocRequestRepository.findAllByRequestTypeAndIsResolvedFalse("changeCourse");
		for (AlocRequest request : requests) {
			User user = request.getUser();
			user.setCourse(Course.FULL);
			userRepository.save(user);
			request.setIsResolvedTrue();
		}
		alocRequestRepository.saveAll(requests);

	}
}
