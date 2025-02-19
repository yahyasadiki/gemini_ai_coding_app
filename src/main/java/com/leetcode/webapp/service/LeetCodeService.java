package com.leetcode.webapp.service;

import com.leetcode.webapp.model.GeminiAIResponse;
import com.leetcode.webapp.model.LeetCodeQuestion;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.io.FileReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LeetCodeService {
    private List<LeetCodeQuestion> questions;

    @Value("${app.csv-file-path}")
    private String csvFilePath;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    @PostConstruct
    public void loadQuestions() {
        try (FileReader reader = new FileReader(csvFilePath)) {
            CsvToBean<LeetCodeQuestion> csvToBean = new CsvToBeanBuilder<LeetCodeQuestion>(reader)
                    .withType(LeetCodeQuestion.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            questions = csvToBean.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LeetCodeQuestion> getAllQuestions() {
        return questions;
    }

    public List<LeetCodeQuestion> getQuestions(String difficulty, String company) {
        return questions.stream()
                .filter(q -> q.getDifficulty().equalsIgnoreCase(difficulty) &&
                        (company == null || company.isEmpty() || q.getCompanies().toLowerCase().contains(company.toLowerCase())))
                .collect(Collectors.toList());
    }

    public LeetCodeQuestion getQuestionById(Long id) {
        return questions.stream().filter(q -> q.getId().equals(id)).findFirst().orElse(null);
    }

    public GeminiAIResponse evaluateCode(String code, LeetCodeQuestion question) {
        if (code == null || code.trim().isEmpty()) {
            return new GeminiAIResponse("Please provide some code to evaluate.", "N/A", 0, "No code provided.");
        }

        try {
            // Construct prompt for Gemini AI
            String prompt = "You are an AI LeetCode code reviewer. Given a problem description and a user's code submission, evaluate the correctness, efficiency, and readability." +
                    "\n\nProblem:\n" + question.getDescription() +
                    "\n\nUser Code:\n" + code +
                    "\n\nProvide:\n1. A rating from 1-10 (1 = incorrect, 10 = perfect)\n2. Feedback on correctness & efficiency\n3. A corrected version of the code if there are errors. Return the corrected code in markdown code blocks. If there is no need for correction, return in markdown that no correction is needed.";

            // Call Gemini API
            GeminiAIResponse geminiResponse = callGeminiAI(prompt);

            // Process AI response
            return geminiResponse; // Return the entire object
        } catch (Exception e) {
            e.printStackTrace();
            return new GeminiAIResponse("Error evaluating code: " + e.getMessage(), "N/A", 0, "Error during evaluation.");
        }
    }


    private GeminiAIResponse callGeminiAI(String prompt) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject();
        JSONObject generationConfig = new JSONObject();
        generationConfig.put("maxOutputTokens", 2048); // Increased for longer responses
        generationConfig.put("temperature", 0.4); // Adjusted temperature for more consistent results
        generationConfig.put("topP", 1);

        JSONObject safetySettings = new JSONObject();
        safetySettings.put("category", "HARM_CATEGORY_HARASSMENT");
        safetySettings.put("threshold", "BLOCK_NONE");

        JSONArray safetySettingsArray = new JSONArray();
        safetySettingsArray.put(safetySettings);
        requestBody.put("safetySettings", safetySettingsArray);

        JSONObject content = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", prompt);
        parts.put(part);
        content.put("parts", parts);

        JSONArray contents = new JSONArray();
        contents.put(content);

        requestBody.put("contents", contents);
        requestBody.put("generationConfig", generationConfig);


        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());

            // Extract the generated text from the JSON response
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject contentResponse = firstCandidate.getJSONObject("content");
            JSONArray partsResponse = contentResponse.getJSONArray("parts");
            JSONObject firstPartResponse = partsResponse.getJSONObject(0);
            String generatedText = firstPartResponse.getString("text");

            // Parse the generated text to extract rating, feedback, and corrected code
            int rating = extractRating(generatedText);
            String feedback = extractFeedback(generatedText);
            String correctedCode = extractCorrectedCode(generatedText);

            GeminiAIResponse geminiResponse = new GeminiAIResponse();
            geminiResponse.setCorrectedCode(correctedCode);
            geminiResponse.setFeedback(feedback);
            geminiResponse.setRating(rating);

            return geminiResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return new GeminiAIResponse("Error processing code.", "No explanation.", 0, "API error.");
        }
    }

    private int extractRating(String text) {
        try {
            // Use regex to find the rating within the text
            String ratingRegex = "(?i)Rating:\\s*(\\d+)/10";
            Pattern pattern = Pattern.compile(ratingRegex);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1)); // Extract the rating value
            } else {
                return 0; // Default value if rating not found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Default value in case of an error
        }
    }

    private String extractFeedback(String text) {
        try {
            // Use regex to find the feedback within the text
            String feedbackRegex = "(?i)Feedback:\\s*(.*?)(Corrected Code:|$)";
            Pattern pattern = Pattern.compile(feedbackRegex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                return matcher.group(1).trim(); // Extract the feedback value
            } else {
                return "No feedback available."; // Default value if feedback not found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting feedback."; // Default value in case of an error
        }
    }

    private String extractCorrectedCode(String text) {
        try {
            // Updated regex to capture code blocks more reliably
            String correctedCodeRegex = "(?i)Corrected Code:\\s*(?:```(\\w+)\n)?(.*?)(?:```|$)";
            Pattern pattern = Pattern.compile(correctedCodeRegex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                String language = matcher.group(1); // Optional: Captures the language of the code block (e.g., python, java)
                String code = matcher.group(2).trim(); // Captures the code
                return code;
            } else {
                return "No correction needed."; // Default value if corrected code not found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting corrected code."; // Default value in case of an error
        }
    }
}