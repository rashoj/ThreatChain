package com.honeynet.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private static final String THREAT_PROMPT_TEMPLATE = """
            You are a cybersecurity analyst. Write a concise summary of this threat:
            
            Name: %s
            Description: %s
            Indicators: %s
            
            The summary should include the threat type, potential impact, and recommended actions.
            """;

    public String generateThreatSummary(String threatName, String description, List<String> indicators) {
        logger.info("⚙️ Calling OpenAI to summarize threat: {}", threatName);
        logger.info("Using OpenAI model: {}", model);

        try {
            // Set timeout
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(5000);
            RestTemplate restTemplate = new RestTemplate(factory);

            // Build prompt
            String prompt = String.format(
                    THREAT_PROMPT_TEMPLATE,
                    threatName,
                    description,
                    String.join(", ", indicators)
            );
            logger.debug("🧠 Prompt sent to OpenAI:\n{}", prompt);

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // Prepare body
            Map<String, Object> userMessage = Map.of(
                    "role", "user",
                    "content", prompt
            );
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(userMessage),
                    "temperature", 0.7
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(OPENAI_URL, entity, JsonNode.class);

            JsonNode body = response.getBody();
            logger.debug("📦 Raw OpenAI response: {}", body);

            JsonNode choices = body != null ? body.get("choices") : null;
            if (response.getStatusCode().is2xxSuccessful() &&
                    choices != null && choices.isArray() && choices.size() > 0) {

                JsonNode messageNode = choices.get(0).get("message");
                if (messageNode != null && messageNode.has("content")) {
                    String summary = messageNode.get("content").asText().trim();
                    return summary.isBlank() ? null : summary;
                }
            }

            logger.warn("⚠️ OpenAI response was unsuccessful or missing content.");
        } catch (HttpStatusCodeException ex) {
            logger.error("❌ OpenAI API error: {}", ex.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("❌ Unexpected error while generating AI summary", e);
        }

        return null;
    }

}
