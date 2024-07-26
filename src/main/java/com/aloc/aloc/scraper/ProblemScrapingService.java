package com.aloc.aloc.scraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.enums.CourseRoutineTier;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problem.service.UserProblemService;
import com.aloc.aloc.problemtag.ProblemTag;
import com.aloc.aloc.problemtag.repository.ProblemTagRepository;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.tag.Tag;
import com.aloc.aloc.tag.repository.TagRepository;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.service.UserService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
	private final UserProblemService userProblemService;
	private final AlgorithmService algorithmService;
	private final ProblemService problemService;
	private final ProblemTypeRepository problemTypeRepository;
	private final ProblemTagRepository problemTagRepository;

	@Transactional
	public String addProblemsForThisWeek()
		throws ExecutionException, InterruptedException {
		Algorithm weeklyAlgorithm = algorithmService.findWeeklyAlgorithm(); // 1주에 5개
		Algorithm dailyAlgorithm = algorithmService.findDailyAlgorithm(); // 1주에 7개

		Map<CourseRoutineTier, List<Integer>> crawledProblems = new HashMap<>();

		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			try {
				crawledProblems.put(CourseRoutineTier.HALF_WEEKLY,
					addProblemsByType(weeklyAlgorithm, CourseRoutineTier.HALF_WEEKLY));
				TimeUnit.SECONDS.sleep(5); // 5초 대기

				crawledProblems.put(CourseRoutineTier.FULL_WEEKLY,
					addProblemsByType(weeklyAlgorithm, CourseRoutineTier.FULL_WEEKLY));
				TimeUnit.SECONDS.sleep(5); // 5초 대기

				crawledProblems.put(CourseRoutineTier.HALF_DAILY,
					addProblemsByType(dailyAlgorithm, CourseRoutineTier.HALF_DAILY));
				TimeUnit.SECONDS.sleep(5); // 5초 대기

				crawledProblems.put(CourseRoutineTier.FULL_DAILY,
					addProblemsByType(dailyAlgorithm, CourseRoutineTier.FULL_DAILY));

			} catch (Exception e) {
				throw new RuntimeException("Error in addProblemsForThisWeek", e);
			}
		});
		future.get();
		updateWeeklyAlgorithmHidden(weeklyAlgorithm);

		return getCrawlingResultMessage(crawledProblems);
	}

	private String getCrawlingResultMessage(Map<CourseRoutineTier, List<Integer>> crawledProblems) {
		StringBuilder message = new StringBuilder();

		for (Map.Entry<CourseRoutineTier, List<Integer>> entry : crawledProblems.entrySet()) {
			CourseRoutineTier tier = entry.getKey();
			List<Integer> problems = entry.getValue();
			message.append(tier).append("\n")
				.append("크롤링 성공 문제수 : ").append(problems.size()).append("\n")
				.append("문제 번호: ").append(problems.stream().map(String::valueOf).collect(Collectors.joining(", ")))
				.append("\n\n");
		}
		return message.toString();
	}

	private void updateWeeklyAlgorithmHidden(Algorithm weeklyAlgorithm) {
		weeklyAlgorithm.setHiddenFalse();
		algorithmService.saveAlgorithm(weeklyAlgorithm);
	}

	private List<Integer> addProblemsByType(Algorithm algorithm, CourseRoutineTier courseRoutineTier)
		throws IOException {
		ProblemType problemType = problemTypeRepository
			.findByCourseAndRoutine(courseRoutineTier.getCourse(), courseRoutineTier.getRoutine())
			.orElseThrow(() -> new NoSuchElementException("해당 문제 타입이 존재하지 않습니다."));

		String url = getProblemUrl(courseRoutineTier, algorithm.getAlgorithmId());
		return crawlAndAddProblems(url, problemType, algorithm, courseRoutineTier.getTargetCount());
	}

	private String getProblemUrl(CourseRoutineTier courseRoutineTier, int algorithmId) {
		String tiers = courseRoutineTier.getTierList().stream()
			.map(Object::toString)
			.collect(Collectors.joining(","));

		return String.format(
			"https://www.acmicpc.net/problemset?sort=ac_desc&tier=%s&algo=%d&algo_if=and",
			tiers,
			algorithmId);
	}

	private List<Integer> crawlAndAddProblems(String url, ProblemType problemType, Algorithm algorithm, int targetCount)
		throws IOException {
		Document document = Jsoup.connect(url).get();
		Elements rows = document.select("tbody tr");

		List<String> problemNumbers = extractProblemNumbers(rows);
		// 문제 목록을 섞습니다.
		Collections.shuffle(problemNumbers);

		// 문제를 하나씩 확인하며 새로운 문제인지 확인합니다. 새로운 문제이면 저장합니다.
		return problemNumbers.stream()
			.filter(problemNumber -> problemService.isNewProblem(problemNumber, problemType, currentSeason))
			.map(problemNumber -> {
				try {
					String problemUrl = getProblemUrl(problemNumber);
					String jsonString = fetchJsonFromUrl(problemUrl);
					return parseAndSaveProblem(jsonString, algorithm, problemType);
				} catch (IOException e) {
					System.err.println("Error fetching problem " + problemNumber + ": " + e.getMessage());
					return null;
				}
			})
			.filter(Objects::nonNull)
			.limit(targetCount)
			.toList();
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

	protected String fetchJsonFromUrl(String url) throws IOException {
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

	public int parseAndSaveProblem(String jsonString, Algorithm algorithm, ProblemType problemType) {
		JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

		String titleKo = extractTitleKo(jsonObject); // 한국어 제목 추출
		if (titleKo == null) {
			return 0;
		}
		int problemId = jsonObject.get("problemId").getAsInt();
		int tier = jsonObject.get("level").getAsInt();

		List<Tag> tagList = extractTags(jsonObject);
		Long createdProblemId = saveProblem(titleKo, tier, problemId, algorithm, problemType, tagList);
		if (createdProblemId != null) {
			saveUserProblem(createdProblemId);
			return 1;
		}
		return 0;
	}

	private String extractTitleKo(JsonObject jsonObject) {
		if (jsonObject.has("titleKo")) {
			return jsonObject.get("titleKo").getAsString();
		} else if (jsonObject.has("titles")) {
			JsonArray titles = jsonObject.getAsJsonArray("titles");
			for (JsonElement titleElement : titles) {
				JsonObject titleObject = titleElement.getAsJsonObject();
				if ("ko".equals(titleObject.get("language").getAsString())) {
					return titleObject.get("title").getAsString();
				}
			}
		}
		// 한국어 제목을 찾지 못한 경우, null을 반환합니다.
		return null;
	}

	private void saveUserProblem(Long problemId) {
		List<User> users = userService.getActiveUsers();
		for (User user : users) {
			userProblemService.saveUserProblem(UserProblem.builder()
				.user(user)
				.problem(problemService.findProblemById(problemId))
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

	@Transactional
	public Long saveProblem(String titleKo, int tier, int problemId, Algorithm algorithm,
		ProblemType problemType, List<Tag> tagList) {
		Problem problem = Problem.builder()
			.title(titleKo)
			.difficulty(tier)
			.problemId(problemId)
			.algorithm(algorithm)
			.problemType(problemType)
			.build();

		for (Tag tag : tagList) {
			ProblemTag problemTag = ProblemTag.builder()
				.problem(problem)
				.tag(tag)
				.build();
			problemTagRepository.save(problemTag);
			problem.addProblemTag(problemTag);
		}
		Problem savedProblem = problemService.saveProblem(problem);
		return savedProblem.getId();
	}
}
