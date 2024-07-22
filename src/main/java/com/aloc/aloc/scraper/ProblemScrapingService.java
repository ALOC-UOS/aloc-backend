package com.aloc.aloc.scraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.enums.CourseRoutineTier;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.problemtag.ProblemTag;
import com.aloc.aloc.problemtag.repository.ProblemTagRepository;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.tag.Tag;
import com.aloc.aloc.tag.repository.TagRepository;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.service.UserService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ProblemScrapingService {

	private static final String HEADER_FIELD_NAME = "User-Agent";
	private static final String HEADER_FIELD_VALUE =
		"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
			+ "(KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";

	@Value("${app.season}")
	private int currentSeason;

	private final TagRepository tagRepository;
	private final UserService userService;
	private final UserProblemRepository userProblemRepository;
	private final AlgorithmRepository algorithmRepository;
	private final ProblemRepository problemRepository;
	private final ProblemTypeRepository problemTypeRepository;
	private final ProblemTagRepository problemTagRepository;

	@Transactional
	public void addProblemsForThisWeek() throws IOException {
		Algorithm weeklyAlgorithm = findWeeklyAlgorithm(); // 1주에 5개
		Algorithm dailyAlgorithm = findDailyAlgorithm(); // 1주에 7개
		System.out.println(weeklyAlgorithm.getName() + dailyAlgorithm.getName());
		addProblemsByType(weeklyAlgorithm, CourseRoutineTier.HALF_WEEKLY);
		addProblemsByType(weeklyAlgorithm, CourseRoutineTier.FULL_WEEKLY);

		addProblemsByType(dailyAlgorithm, CourseRoutineTier.HALF_DAILY);
		addProblemsByType(dailyAlgorithm, CourseRoutineTier.FULL_DAILY);
		updateWeeklyAlgorithmHidden(weeklyAlgorithm);
	}

	private void updateWeeklyAlgorithmHidden(Algorithm weeklyAlgorithm) {
		weeklyAlgorithm.setHiddenFalse();
		algorithmRepository.save(weeklyAlgorithm);
	}

	public Algorithm findWeeklyAlgorithm() {
		return algorithmRepository.findFirstBySeasonAndHiddenTrueOrderByCreatedAtAsc(currentSeason)
			.orElseThrow(() -> new NoSuchElementException("해당 시즌의 공개되지 않은 알고리즘이 존재하지 않습니다."));
	}

	public Algorithm findDailyAlgorithm() {
		return algorithmRepository.findFirstBySeasonAndHiddenFalseOrderByCreatedAtDesc(currentSeason)
		.orElseThrow(() -> new NoSuchElementException("공개된 알고리즘이 존재하지 않습니다."));
	}

	private void addProblemsByType(Algorithm algorithm, CourseRoutineTier courseRoutineTier)
		throws IOException {
		ProblemType problemType = problemTypeRepository
			.findByCourseAndRoutine(courseRoutineTier.getCourse(), courseRoutineTier.getRoutine())
			.orElseThrow(() -> new NoSuchElementException("해당 문제 타입이 존재하지 않습니다."));
		for (int tier : courseRoutineTier.getTierList()) {
			String url = getProblemUrl(tier, algorithm.getAlgorithmId());
			crawlAndAddProblems(url, problemType, tier, algorithm);
		}
	}

	private String getProblemUrl(int tier, int algorithmId) {
		return String.format(
			"https://www.acmicpc.net/problemset?sort=ac_desc&tier=%d&algo=%d&algo_if=and", tier,
			algorithmId);
	}

	private void crawlAndAddProblems(String url, ProblemType problemType, int tier, Algorithm algorithm)
		throws IOException {
		Document document = Jsoup.connect(url).get();
		Elements rows = document.select("tbody tr");

		List<String> problemNumbers = extractProblemNumbers(rows);

		for (String problemNumber : problemNumbers) {
			String problemUrl = getProblemUrl(problemNumber);
			String jsonString = fetchJsonFromUrl(problemUrl);
			if (isNewProblem(problemNumber, problemType)) {
				parseAndSaveProblem(jsonString, tier, algorithm, problemType);
				return;
			}
		}

		if (problemNumbers.isEmpty()) {
			// 한단계 높인 티어로 다시 크롤링
			String retryUrl = getProblemUrl(tier + 1, algorithm.getAlgorithmId());
			crawlAndAddProblems(retryUrl, problemType, tier + 1, algorithm);
		}
	}

	private List<String> extractProblemNumbers(Elements rows) {
		List<String> problemNumbers = new ArrayList<>();
		for (Element row : rows) {
			problemNumbers.add(row.select(".list_problem_id").text());
		}
		return problemNumbers;
	}

	private String getProblemUrl(String problemNumber) {
		return String.format("https://solved.ac/api/v3/problem/show?problemId=%s", problemNumber);
	}

	private String fetchJsonFromUrl(String url) throws IOException {
		int maxRetries = 3; // 최대 3번까지 재시도
		int retryDelayMs = 5000; // 5초

		for (int attempt = 0; attempt < maxRetries; attempt++) {
			try {
				HttpURLConnection connection = createConnection(url);
				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					return readResponse(connection);
				} else if (responseCode == 429) { // Too Many Requests
					// API 제한에 걸린 경우, 더 오래 기다립니다.
					Thread.sleep(retryDelayMs * 2);
				} else {
					System.out.println("HTTP Error: " + responseCode + " for URL: " + url);
				}
			} catch (IOException e) {
				System.out.println("Attempt " + (attempt + 1) + " failed: " + e.getMessage());
				if (attempt == maxRetries - 1) {
					throw e; // 마지막 시도에서 실패하면 예외를 던집니다.
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IOException("Request interrupted", e);
			}

			// 재시도 전 대기
			try {
				Thread.sleep(retryDelayMs);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IOException("Sleep interrupted", e);
			}
		}

		throw new IOException("Failed to fetch data after " + maxRetries + " attempts");
	}

	private HttpURLConnection createConnection(String url) throws IOException {
		URL apiUrl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty(HEADER_FIELD_NAME, HEADER_FIELD_VALUE);
		return connection;
	}

	private String readResponse(HttpURLConnection connection) throws IOException {
		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
			new InputStreamReader(connection.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		}
		return response.toString();
	}

	private boolean isNewProblem(String problemNumber, ProblemType problemType) {
		Integer problemId = Integer.parseInt(problemNumber);
		return !problemRepository.existsByProblemIdAndProblemType_Course(problemId,
			problemType.getCourse());
	}

	private void parseAndSaveProblem(String jsonString, int tier, Algorithm algorithm, ProblemType problemType) {
		JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
		if (jsonObject.has("titles")) {
			JsonArray titles = jsonObject.getAsJsonArray("titles");
			JsonObject titleObject = titles.get(0).getAsJsonObject();
			if ("ko".equals(titleObject.get("language").getAsString())) {
				String titleKo = jsonObject.get("titleKo").getAsString();
				int problemId = jsonObject.get("problemId").getAsInt();
				List<Tag> tagList = extractTags(jsonObject);
				Long createdProblemId = saveProblem(titleKo, tier, problemId, algorithm, problemType, tagList);
				if (createdProblemId != null) {
					saveUserProblem(createdProblemId);
				}
			}
		}
	}

	private void saveUserProblem(Long problemId) {
		List<User> users = userService.getActiveUsers();
		for (User user : users) {
			userProblemRepository.save(UserProblem.builder()
				.user(user)
				.problem(problemRepository.getReferenceById(problemId))
				.isSolved(false)
				.season(currentSeason)
				.build());
		}
	}

	private List<Tag> extractTags(JsonObject jsonObject) {
		List<Tag> tagList = new ArrayList<>();
		JsonArray tagsArray = jsonObject.getAsJsonArray("tags");
		for (int i = 0; i < tagsArray.size(); i++) {
			JsonObject tagObject = tagsArray.get(i).getAsJsonObject();
			JsonArray displayNames = tagObject.getAsJsonArray("displayNames");
			String koreanName = displayNames.get(0).getAsJsonObject().get("name").getAsString();
			String englishName = displayNames.get(1).getAsJsonObject().get("name").getAsString();
			Tag tag = findOrCreateTag(koreanName, englishName);
			tagList.add(tag);
		}
		return tagList;
	}

	private Tag findOrCreateTag(String koreanName, String englishName) {
		return tagRepository.findByKoreanNameAndEnglishName(koreanName, englishName)
			.orElseGet(() -> {
				Tag newTag = Tag.builder()
					.koreanName(koreanName)
					.englishName(englishName)
					.build();
				return tagRepository.save(newTag);
			});
	}

	private Long saveProblem(String titleKo, int tier, int problemId, Algorithm algorithm,
		ProblemType problemType, List<Tag> tagList) {
		Problem problem = Problem.builder()
			.title(titleKo)
			.difficulty(tier)
			.problemId(problemId)
			.algorithm(algorithm)
			.problemType(problemType)
			.build();
		problemRepository.save(problem);

		for (Tag tag : tagList) {
			ProblemTag problemTag = ProblemTag.builder()
				.problem(problem)
				.tag(tag)
				.build();
			problemTagRepository.save(problemTag);
			problem.addProblemTag(problemTag);
		}
		problemRepository.save(problem);
		return problem.getId();
	}
}
