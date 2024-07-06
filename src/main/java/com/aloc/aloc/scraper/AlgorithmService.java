package com.aloc.aloc.algorithm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.aloc.aloc.algorithm.Algorithm;
import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlgorithmService {

	private final AlgorithmRepository algorithmRepository;

	public List<AlgorithmResponseDto> getAlgorithms() {
		List<Algorithm> algorithms = algorithmRepository.findAll(Sort.by(Direction.DESC, "id"));
		return makeAlgorithmResponse(algorithms);
	}

	public List<AlgorithmResponseDto> getAlgorithmsBySeason(int season) {
		List<Algorithm> algorithms = algorithmRepository.findAllBySeasonOrderByIdDesc(season);
		return makeAlgorithmResponse(algorithms);
	}

	private List<AlgorithmResponseDto> makeAlgorithmResponse(List<Algorithm> algorithms) {
		List<AlgorithmResponseDto> response = new ArrayList<>();
		int index = 1;
		for (Algorithm algorithm : algorithms) {
			AlgorithmResponseDto dto = AlgorithmResponseDto.builder()
				.id(index++)
				.algorithmId(algorithm.getAlgorithmId())
				.name(algorithm.getName())
				.season(algorithm.getSeason())
				.hidden(algorithm.getHidden())
				.build();
			response.add(dto);
		}
		return response;
	}

}
