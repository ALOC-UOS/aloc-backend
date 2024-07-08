package com.aloc.aloc.algorithm.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlgorithmService {

	private final AlgorithmRepository algorithmRepository;

	public List<AlgorithmResponseDto> getAlgorithms() {
		List<Algorithm> algorithms = algorithmRepository.findAll(Sort.by(Direction.DESC, "id"));
		return AlgorithmResponseDto.listOf(algorithms);
	}

	public List<AlgorithmResponseDto> getAlgorithmsBySeason(int season) {
		List<Algorithm> algorithms = algorithmRepository.findAllBySeasonOrderByIdDesc(season);
		return AlgorithmResponseDto.listOf(algorithms);
	}



}
