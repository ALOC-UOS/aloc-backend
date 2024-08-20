package com.aloc.aloc.scraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
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
import com.aloc.aloc.global.apipayload.exception.ScrapException;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problemtag.ProblemTag;
import com.aloc.aloc.problemtag.repository.ProblemTagRepository;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.tag.Tag;
import com.aloc.aloc.tag.repository.TagRepository;
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
	private static final int RUBY_1 = 30;

	@Value("${app.season}")
	private int currentSeason;

	private final TagRepository tagRepository;
	private final AlgorithmService algorithmService;
	private final ProblemService problemService;
	private final ProblemTypeRepository problemTypeRepository;
	private final ProblemTagRepository problemTagRepository;

	@Transactional
	public String addProblemsForThisWeek()
		throws ExecutionException, InterruptedException {
		Algorithm lastWeeklyAlgorithm = algorithmService.findWeeklyAlgorithm(); // 지난 주차의 weekly Hidden False로 변경
		updateWeeklyAlgorithmHidden(lastWeeklyAlgorithm);
		Algorithm weeklyAlgorithm = algorithmService.findWeeklyAlgorithm(); // 1주에 5개 새로운 주차의 weekly Algorithm
		Algorithm dailyAlgorithm = algorithmService.findDailyAlgorithm(); // 1주에 7개

		Map<CourseRoutineTier, List<Problem>> crawledProblems = new LinkedHashMap<>();

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
		return getCrawlingResultMessage(crawledProblems, weeklyAlgorithm, dailyAlgorithm);
	}

	private String getCrawlingResultMessage(Map<CourseRoutineTier, List<Problem>> crawledProblems,
		Algorithm weeklyAlgorithm, Algorithm dailyAlgorithm) {
		StringBuilder message = new StringBuilder();
		message.append("[ ").append(weeklyAlgorithm.getWeek()).append("주차 크롤링 결과 ]\n\n")
			.append("weekly 알고리즘 : ").append(weeklyAlgorithm.getName()).append("\n")
			.append("daily 알고리즘 : ").append(dailyAlgorithm.getName()).append("\n\n");
		for (Map.Entry<CourseRoutineTier, List<Problem>> entry : crawledProblems.entrySet()) {
			CourseRoutineTier tier = entry.getKey();
			List<Problem> problems = entry.getValue();
			message.append("[").append(tier).append("]").append("\n")
				.append("✅  크롤링 성공 문제수: ").append(problems.size()).append("개\n")
				.append("🔢  문제 번호: ").append(problems.stream().map(String::valueOf).collect(Collectors.joining(", ")))
				.append("\n\n");

			for (Problem problem : problems) {
				message.append("   - ")
					.append(problem.getProblemId())
					.append(": ")
					.append(problem.getTitle())
					.append("\n");
			}
		}
		return message.toString();
	}

	private void updateWeeklyAlgorithmHidden(Algorithm weeklyAlgorithm) {
		weeklyAlgorithm.setHiddenFalse();
		algorithmService.saveAlgorithm(weeklyAlgorithm);
	}

	@Transactional
	public List<Problem> addProblemsByType(Algorithm algorithm, CourseRoutineTier courseRoutineTier)
		throws IOException {
		ProblemType problemType = problemTypeRepository
			.findByCourseAndRoutine(courseRoutineTier.getCourse(), courseRoutineTier.getRoutine())
			.orElseThrow(() -> new NoSuchElementException("해당 문제 타입이 존재하지 않습니다."));

		List<Integer> tierList = new ArrayList<>(courseRoutineTier.getTierList());
		List<Problem> crawledProblems = new ArrayList<>();
		int targetCount = courseRoutineTier.getTargetCount();
		int maxTier = tierList.get(tierList.size() - 1);

		while (true) {
			String url = getProblemUrl(tierList, algorithm.getAlgorithmId());
			crawledProblems.addAll(crawlProblems(url, problemType, algorithm));

			crawledProblems = filterDuplicateProblems(crawledProblems);
			if (crawledProblems.size() >= targetCount) {
				return saveAndSortProblems(crawledProblems.subList(0, targetCount), problemType, algorithm);
			}

			crawledProblems = new ArrayList<>();
			maxTier++;

			if (maxTier > RUBY_1) { // 가장 높은 단계까지 포함하여 조회해도 문제 수가 targetCount 충족 못할 때
				throw new ScrapException("스크롤 할 문제가 부족합니다. 수동으로 문제 추가해주세요.");
			}
			tierList.add(maxTier);
		}
	}

	private String getProblemUrl(List<Integer> tierList, int algorithmId) {
		String tiers = tierList.stream()
			.map(Object::toString)
			.collect(Collectors.joining(","));

		return String.format(
			"https://www.acmicpc.net/problemset?sort=ac_desc&tier=%s&algo=%d&algo_if=and",
			tiers,
			algorithmId);
	}

	@Transactional
	public List<Problem> crawlProblems(String url, ProblemType problemType, Algorithm algorithm)
		throws IOException {
		Document document = Jsoup.connect(url).get();
		Elements rows = document.select("tbody tr");

		List<Integer> problemNumbers = extractProblemNumbers(rows);
		// 문제 목록을 섞습니다.
		Collections.shuffle(problemNumbers);

		// 문제를 하나씩 확인하며 새로운 문제인지 확인합니다.
		return problemNumbers.stream()
			.filter(problemNumber -> problemService.isNewProblem(problemNumber, problemType, currentSeason))
			.map(problemNumber -> {
				try {
					String problemUrl = getProblemUrl(problemNumber);
					String jsonString = fetchJsonFromUrl(problemUrl);
					return parseProblem(jsonString, algorithm, problemType);
				} catch (Exception e) {
					System.err.println("Error fetching problem " + problemNumber + ": " + e.getMessage());
					return null;
				}
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	private List<Problem> filterDuplicateProblems(List<Problem> crawledProblems) {
		return crawledProblems.stream()
			.filter(problem -> problemService.isNewProblem(problem.getProblemId(),
				problem.getProblemType(), currentSeason))
			.distinct()
			.collect(Collectors.toList());
	}

	@Transactional
	public List<Problem> saveAndSortProblems(List<Problem> problems, ProblemType problemType,
		Algorithm algorithm) {
		problems.sort(Comparator.comparingInt(Problem::getDifficulty));

		return problems.stream()
			.map(problem -> saveProblem(problem.getTitle(), problem.getDifficulty(),
				problem.getProblemId(), algorithm, problemType, problem.getProblemTagList()))
			.collect(Collectors.toList());
	}

	private Problem parseProblem(String jsonString, Algorithm algorithm, ProblemType problemType) {
		JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

		String titleKo = extractTitleKo(jsonObject); // 한국어 제목 추출
		if (titleKo == null) {
			throw new IllegalArgumentException("Korean title not found in JSON: " + jsonString);
		}
		int problemId = jsonObject.get("problemId").getAsInt();
		int tier = jsonObject.get("level").getAsInt();

		Problem problem = Problem.builder()
			.title(titleKo)
			.difficulty(tier)
			.problemId(problemId)
			.algorithm(algorithm)
			.problemType(problemType)
			.build();
		// ProblemTag 리스트를 생성하며 Problem 객체와 연결
		List<ProblemTag> problemTagList = extractTags(jsonObject).stream()
			.map(tag -> ProblemTag.builder()
				.problem(problem)  // 여기서 이미 Problem과 연결
				.tag(tag)
				.build())
			.toList();
		problem.setProblemTagList(problemTagList);
		return problem;
	}

	private List<Integer> extractProblemNumbers(Elements rows) {
		List<Integer> problemNumbers = new ArrayList<>();
		int count = 0;
		for (Element row : rows) {
			if (count >= 25) {
				break;  // 100개에 도달하면 루프 종료
			}
			String problemIdText = row.select(".list_problem_id").text();
			int problemId = Integer.parseInt(problemIdText);
			problemNumbers.add(problemId);
			count++;
		}
		return problemNumbers;
	}

	private String extractTitleKo(JsonObject jsonObject) {
		if (jsonObject.has("titles")) {
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
	public Problem saveProblem(String titleKo, int tier, int problemId, Algorithm algorithm,
		ProblemType problemType, List<ProblemTag> problemTagList) {
		Problem problem = Problem.builder()
			.title(titleKo)
			.difficulty(tier)
			.problemId(problemId)
			.algorithm(algorithm)
			.problemType(problemType)
			.build();

		problemService.saveProblem(problem);
		for (ProblemTag problemTag : problemTagList) {
			problemTag.setProblem(problem);
			problemTagRepository.save(problemTag);
			problem.addProblemTag(problemTag);
		}
		return problem;
	}

	@Transactional
	public Problem getProblemByProblemId(
		int problemId,
		Algorithm algorithm,
		ProblemType problemType) {
		String url = getProblemUrl(problemId);
		System.out.println("url: " + url);
		try {
			return crawlAndAddProblem(url, problemType, algorithm);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional
	public Problem crawlAndAddProblem(String problemUrl, ProblemType problemType, Algorithm algorithm)
		throws IOException {
		String jsonString = fetchJsonFromUrl(problemUrl);
		Problem problem = parseProblem(jsonString, algorithm, problemType);
		return saveProblem(problem.getTitle(), problem.getDifficulty(),
				problem.getProblemId(), algorithm, problemType, problem.getProblemTagList());
	}

	private String getProblemUrl(int problemNumber) {
		return String.format("https://solved.ac/api/v3/problem/show?problemId=%d", problemNumber);
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
}
