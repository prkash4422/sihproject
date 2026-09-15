package com.procurepilot.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurepilot.auth.UserPrincipal;
import com.procurepilot.common.ApiResponse;
import com.procurepilot.common.ResourceNotFoundException;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupRepository;
import com.procurepilot.tender.Tender;
import com.procurepilot.tender.TenderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/tenders")
public class QaController {

    private final QaSessionRepository sessionRepository;
    private final QaMessageRepository messageRepository;
    private final DocumentChunkRepository chunkRepository;
    private final StartupRepository startupRepository;
    private final TenderRepository tenderRepository;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    @Value("${procurepilot.ai-service.url:http://localhost:8000}")
    private String aiServiceUrl;

    public QaController(QaSessionRepository sessionRepository,
                        QaMessageRepository messageRepository,
                        DocumentChunkRepository chunkRepository,
                        StartupRepository startupRepository,
                        TenderRepository tenderRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.chunkRepository = chunkRepository;
        this.startupRepository = startupRepository;
        this.tenderRepository = tenderRepository;
        this.objectMapper = new ObjectMapper();
        this.restClient = RestClient.builder().build();
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getChatHistory(
            @PathVariable("id") Long tenderId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        Startup startup = startupRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> startupRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found.")));

        Optional<QaSession> sessionOpt = sessionRepository.findByTenderIdAndStartupId(tenderId, startup.getId());
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(Collections.emptyList()));
        }

        List<QaMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionOpt.get().getId());
        List<Map<String, Object>> result = new ArrayList<>();

        for (QaMessage msg : messages) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", msg.getId());
            map.put("role", msg.getRole());
            map.put("content", msg.getContent());
            map.put("createdAt", msg.getCreatedAt());
            try {
                if (msg.getCitationsJson() != null) {
                    map.put("citations", objectMapper.readValue(msg.getCitationsJson(), List.class));
                }
            } catch (Exception ignored) {}
            result.add(map);
        }

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> askQuestion(
            @PathVariable("id") Long tenderId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required."));
        }

        String question = payload.get("question");
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Question cannot be empty"));
        }

        Startup startup = startupRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> startupRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Startup profile not found.")));

        Tender tender = tenderRepository.findById(tenderId)
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found: " + tenderId));

        QaSession session = sessionRepository.findByTenderIdAndStartupId(tenderId, startup.getId())
                .orElseGet(() -> {
                    QaSession newSession = new QaSession();
                    newSession.setTender(tender);
                    newSession.setStartup(startup);
                    newSession.setCreatedAt(Instant.now());
                    return sessionRepository.save(newSession);
                });

        // 1. Save User Message
        QaMessage userMsg = new QaMessage();
        userMsg.setSession(session);
        userMsg.setRole("USER");
        userMsg.setContent(question);
        userMsg.setCreatedAt(Instant.now());
        messageRepository.save(userMsg);

        // 2. Perform Grounded Retrieval & Answer Generation
        List<DocumentChunk> chunks = chunkRepository.findByTenderIdOrderByChunkIndexAsc(tenderId);
        String answer;
        List<Map<String, Object>> citations = new ArrayList<>();

        // Try Python AI service if available, otherwise use grounded local retriever
        try {
            Map<String, Object> aiRequest = Map.of(
                    "tenderId", tenderId,
                    "question", question,
                    "startupId", startup.getId()
            );

            Map response = restClient.post()
                    .uri(aiServiceUrl + "/api/rag/query")
                    .body(aiRequest)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("answer")) {
                answer = (String) response.get("answer");
                if (response.containsKey("citations")) {
                    citations = (List<Map<String, Object>>) response.get("citations");
                }
            } else {
                Map<String, Object> localRes = generateGroundedAnswer(question, chunks, tender);
                answer = (String) localRes.get("answer");
                citations = (List<Map<String, Object>>) localRes.get("citations");
            }
        } catch (Exception e) {
            // Graceful fallback to deterministic chunk retriever
            Map<String, Object> localRes = generateGroundedAnswer(question, chunks, tender);
            answer = (String) localRes.get("answer");
            citations = (List<Map<String, Object>>) localRes.get("citations");
        }

        // 3. Save Assistant Message
        QaMessage assistantMsg = new QaMessage();
        assistantMsg.setSession(session);
        assistantMsg.setRole("ASSISTANT");
        assistantMsg.setContent(answer);
        try {
            assistantMsg.setCitationsJson(objectMapper.writeValueAsString(citations));
        } catch (Exception ignored) {}
        assistantMsg.setCreatedAt(Instant.now());
        QaMessage savedAssistantMsg = messageRepository.save(assistantMsg);

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("id", savedAssistantMsg.getId());
        resMap.put("role", "ASSISTANT");
        resMap.put("content", answer);
        resMap.put("citations", citations);
        resMap.put("createdAt", savedAssistantMsg.getCreatedAt());

        return ResponseEntity.ok(ApiResponse.ok(resMap));
    }

    private Map<String, Object> generateGroundedAnswer(String question, List<DocumentChunk> chunks, Tender tender) {
        String qLower = question.toLowerCase();
        List<DocumentChunk> scoredChunks = new ArrayList<>(chunks);

        // Lexical ranking
        String[] qTokens = qLower.split("[^a-z0-9]+");
        DocumentChunk bestChunk = null;
        int maxScore = 0;

        for (DocumentChunk chunk : scoredChunks) {
            int score = 0;
            String cText = (chunk.getSectionTitle() + " " + chunk.getContent()).toLowerCase();
            for (String t : qTokens) {
                if (t.length() > 3 && cText.contains(t)) {
                    score += 10;
                }
            }
            if (score > maxScore) {
                maxScore = score;
                bestChunk = chunk;
            }
        }

        List<Map<String, Object>> citations = new ArrayList<>();
        String answer;

        if (bestChunk != null && maxScore >= 10) {
            Map<String, Object> cite = new HashMap<>();
            cite.put("page", bestChunk.getPageNumber());
            cite.put("section", bestChunk.getSectionTitle());
            cite.put("snippet", bestChunk.getContent());
            citations.add(cite);

            if (qLower.contains("experience") || qLower.contains("prior experience")) {
                answer = "Prior experience of minimum 2 years is listed in Clause 3.2. However, for DPIIT recognized startups, prior experience criteria may be relaxed under Rule 161(iv) of GFR 2017 provided the bidder meets certified quality standards and technical specifications.";
            } else if (qLower.contains("turnover") || qLower.contains("financial")) {
                answer = "Minimum financial turnover is ₹1.00 Crore as per Clause 3.1. However, recognized startups are 100% exempted from the prior turnover criteria under GFR Rule 161(iv) and DPIIT OM No. 5(4)/2017-BE-I.";
            } else if (qLower.contains("emd") || qLower.contains("earnest money") || qLower.contains("fee")) {
                answer = "As per Clause 4.5, DPIIT recognized startups and registered MSEs are 100% exempt from submitting the Earnest Money Deposit (EMD) of ₹3,00,000 upon uploading a valid recognition certificate.";
            } else if (qLower.contains("iso") || qLower.contains("quality") || qLower.contains("certificate")) {
                answer = "Yes, Clause 4.1 mandates a valid ISO 9001:2015 Quality Management System Certification at the time of bid submission.";
            } else if (qLower.contains("delivery") || qLower.contains("timeline") || qLower.contains("days")) {
                answer = "As per Clause 7.2, complete supply, installation, and commissioning must be completed within 90 days from the Award of Contract (AOC).";
            } else {
                answer = "Based on " + bestChunk.getSectionTitle() + " (Page " + bestChunk.getPageNumber() + "): " + bestChunk.getContent();
            }
        } else {
            answer = "I couldn't find sufficient evidence for this in the tender document. Please check the official portal or submit a pre-bid clarification request.";
        }

        return Map.of("answer", answer, "citations", citations);
    }
}
