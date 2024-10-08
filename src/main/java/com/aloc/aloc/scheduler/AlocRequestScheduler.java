package com.aloc.aloc.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.alocrequest.AlocRequest;
import com.aloc.aloc.alocrequest.repository.AlocRequestRepository;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlocRequestScheduler {
	private final AlocRequestRepository alocRequestRepository;
	private final UserRepository userRepository;

	@Transactional
	@Scheduled(cron = "0 50 23 * * TUE")
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
