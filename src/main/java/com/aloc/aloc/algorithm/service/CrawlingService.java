package com.aloc.aloc.algorithm.service;

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
import org.springframework.stereotype.Service;

import com.aloc.aloc.algorithm.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problemtag.ProblemTag;
import com.aloc.aloc.problemtag.repository.ProblemTagRepository;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.tag.Tag;
import com.aloc.aloc.tag.repository.TagRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CrawlingService {

	private static final String HEADER_FIELD_NAME = "User-Agent";
	private static final String HEADER_FIELD_VALUE =
		"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
			+ "(KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";
	private static final int[] HALF_WEEKLY = {5, 6, 7, 8, 9};
	private static final int[] HALF_DAILY = {7, 7, 7, 8, 8};
	private static final int[] FULL_WEEKLY = {9, 10, 11, 12, 13};
	private static final int[] FULL_DAILY = {11, 11, 11, 12, 12};

	private static final int SEASON = 2;

	private final TagRepository tagRepository;
	private final AlgorithmRepository algorithmRepository;
	private final ProblemRepository problemRepository;
	private final ProblemTypeRepository problemTypeRepository;
	private final ProblemTagRepository problemTagRepository;

	public void addProblemsForThisWeek() throws IOException {
		Algorithm weeklyAlgorithm = findWeeklyAlgorithm();
		Algorithm dailyAlgorithm = findDailyAlgorithm();

		addProblemsByType(weeklyAlgorithm.getAlgorithmId(), Course.HALF, Routine.WEEKLY,
			HALF_WEEKLY);
		addProblemsByType(weeklyAlgorithm.getAlgorithmId(), Course.FULL, Routine.WEEKLY,
			FULL_WEEKLY);

		addProblemsByType(dailyAlgorithm.getAlgorithmId(), Course.HALF, Routine.DAILY, HALF_DAILY);
		addProblemsByType(dailyAlgorithm.getAlgorithmId(), Course.FULL, Routine.DAILY, FULL_DAILY);
	}

	private Algorithm findWeeklyAlgorithm() {
		return algorithmRepository.findFirstBySeasonAndHiddenTrueOrderByIdAsc(SEASON)
			.orElseThrow(() -> new NoSuchElementException("해당 시즌의 공개되지 않은 알고리즘이 존재하지 않습니다."));
	}

	private Algorithm findDailyAlgorithm() {
		return algorithmRepository.findLastBySeasonAndHiddenFalseOrderByIdDesc(SEASON)
			.orElseGet(
				() -> algorithmRepository.findLastBySeasonAndHiddenFalseOrderByIdDesc(SEASON - 1)
					.orElseThrow(() -> new NoSuchElementException("공개된 알고리즘이 존재하지 않습니다.")));
	}

	private void addProblemsByType(int algorithmId, Course course, Routine routine, int[] tierList)
		throws IOException {
		ProblemType problemType = problemTypeRepository.findByCourseAndRoutine(course, routine);
		for (int tier : tierList) {
			String url = getProblemUrl(tier, algorithmId);
			crawlAndAddProblems(url, problemType, tier, algorithmId);
		}
	}

	private String getProblemUrl(int tier, int algorithmId) {
		return String.format(
			"https://www.acmicpc.net/problemset?sort=ac_desc&tier=%d&algo=%d&algo_if=and", tier,
			algorithmId);
	}

	private void crawlAndAddProblems(String url, ProblemType problemType, int tier, int algorithmId)
		throws IOException {
		Document document = Jsoup.connect(url).get();
		Elements rows = document.select("tbody tr");

		List<String> problemNumbers = extractProblemNumbers(rows);

		for (String problemNumber : problemNumbers) {
			String problemUrl = getProblemUrl(problemNumber);
			String jsonString = fetchJsonFromUrl(problemUrl);
			if (isNewProblem(problemNumber, problemType)) {
				parseAndSaveProblem(jsonString, tier, algorithmId, problemType);
				return;
			}
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
		HttpURLConnection connection = createConnection(url);
		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			return readResponse(connection);
		} else {
			throw new IOException("HTTP Error: " + responseCode);
		}
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
		int problemId = Integer.parseInt(problemNumber);
		return !problemRepository.existsByAlgorithmIdAndProblemType_Course(problemId,
			problemType.getCourse());
	}

	private void parseAndSaveProblem(String jsonString, int tier, int algorithmId,
		ProblemType problemType) {
		JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
		if (jsonObject.has("titles")) {
			JsonArray titles = jsonObject.getAsJsonArray("titles");
			JsonObject titleObject = titles.get(0).getAsJsonObject();
			if ("ko".equals(titleObject.get("language").getAsString())) {
				String titleKo = jsonObject.get("titleKo").getAsString();
				int problemId = jsonObject.get("problemId").getAsInt();
				List<Tag> tagList = extractTags(jsonObject);
				saveProblem(titleKo, tier, problemId, algorithmId, problemType, tagList);
			}
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

	private void saveProblem(String titleKo, int tier, int problemId, int algorithmId,
		ProblemType problemType, List<Tag> tagList) {
		Problem problem = Problem.builder()
			.title(titleKo)
			.difficulty(tier)
			.problemId(problemId)
			.algorithmId(algorithmId)
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
	}
}
