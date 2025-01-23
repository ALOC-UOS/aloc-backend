package com.aloc.aloc.alocrequest.repository;

import com.aloc.aloc.alocrequest.AlocRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlocRequestRepository extends JpaRepository<AlocRequest, Long> {
  List<AlocRequest> findAllByRequestTypeAndIsResolvedFalse(String requestType);
}
