package com.aloc.aloc.alocrequest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aloc.aloc.alocrequest.AlocRequest;

public interface AlocRequestRepository extends JpaRepository<AlocRequest, Long> {
	List<AlocRequest> findAllByRequestTypeAndIsResolvedFalse(String requestType);
}
