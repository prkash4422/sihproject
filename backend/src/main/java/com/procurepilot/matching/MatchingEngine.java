package com.procurepilot.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurepilot.eligibility.DeterministicEligibilityEngine;
import com.procurepilot.eligibility.EligibilityEvaluation;
import com.procurepilot.eligibility.EligibilityResult;
import com.procurepilot.requirement.TenderRequirement;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupCapability;
import com.procurepilot.startup.StartupCertification;
import com.procurepilot.startup.StartupDocument;
import com.procurepilot.tender.Tender;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MatchingEngine {

    private final DeterministicEligibilityEngine eligibilityEngine;
    private final ObjectMapper objectMapper;

    public MatchingEngine(DeterministicEligibilityEngine eligibilityEngine) {
        this.eligibilityEngine = eligibilityEngine;
        this.objectMapper = new ObjectMapper();
    }

    public StartupTenderMatch calculateMatch(Startup startup, Tender tender, List<TenderRequirement> requirements) {
        StartupTenderMatch match = new StartupTenderMatch();
        match.setStartup(startup);
        match.setTender(tender);

        List<EligibilityEvaluation> evaluations = new ArrayList<>();
        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        int passCount = 0;
        int reviewCount = 0;
        int failCount = 0;
        int missingCount = 0;

        for (TenderRequirement req : requirements) {
            EligibilityEvaluation eval = eligibilityEngine.evaluateRequirement(startup, req);
            eval.setMatch(match);
            evaluations.add(eval);

            if (eval.getResult() == EligibilityResult.PASS) {
                passCount++;
                if (eval.getAppliedRelaxationRule() != null) {
                    strengths.add("Startup qualified for relaxation under " + eval.getAppliedRelaxationRule().getRuleCode() + ": " + req.getType());
                } else if (req.getNormalizedValue() != null) {
                    strengths.add("Satisfies " + req.getType() + " requirement: " + eval.getStartupValue());
                }
            } else if (eval.getResult() == EligibilityResult.NEEDS_REVIEW) {
                reviewCount++;
                gaps.add("Review required for " + req.getType() + " (Page " + req.getSourcePage() + "): " + eval.getReason());
            } else if (eval.getResult() == EligibilityResult.FAIL) {
                failCount++;
                gaps.add("Failed criterion for " + req.getType() + ": " + eval.getReason());
            } else if (eval.getResult() == EligibilityResult.MISSING_DATA) {
                missingCount++;
                gaps.add("Missing profile evidence for " + req.getType() + ": " + eval.getReason());
            }
        }

        match.setEvaluations(evaluations);

        // 1. Technical Score (40%)
        int technicalScore = calculateTechnicalScore(startup, tender, requirements, strengths);

        // 2. Sector Relevance (25%)
        int sectorScore = calculateSectorScore(startup, tender);

        // 3. Eligibility Completeness (20%)
        int totalEval = Math.max(1, requirements.size());
        int eligibilityScore = Math.min(100, (int) Math.round(((passCount * 1.0 + reviewCount * 0.5) / totalEval) * 100));

        // 4. Document Readiness (10%)
        int readinessScore = calculateDocumentReadiness(startup, requirements);

        // 5. Opportunity Fit (5%)
        int fitScore = calculateFitScore(startup, tender);

        // Overall Weighted Calculation
        // 40% Technical + 25% Sector + 20% Eligibility + 10% Readiness + 5% Fit
        int overallScore = (int) Math.round(
                (technicalScore * 0.40) +
                (sectorScore * 0.25) +
                (eligibilityScore * 0.20) +
                (readinessScore * 0.10) +
                (fitScore * 0.05)
        );

        match.setTechnicalScore(technicalScore);
        match.setSectorScore(sectorScore);
        match.setEligibilityScore(eligibilityScore);
        match.setReadinessScore(readinessScore);
        match.setFitScore(fitScore);
        match.setOverallScore(Math.min(100, Math.max(0, overallScore)));

        // Generate explainable narrative
        Map<String, Object> explanation = new HashMap<>();
        String summary = generateSummary(overallScore, startup, tender, passCount, failCount, reviewCount);
        explanation.put("summary", summary);
        explanation.put("strengths", strengths.isEmpty() ? Collections.singletonList("General alignment with tender scope") : strengths);
        explanation.put("gaps", gaps.isEmpty() ? Collections.singletonList("No critical compliance blockers identified") : gaps);

        try {
            match.setExplanationJson(objectMapper.writeValueAsString(explanation));
        } catch (Exception e) {
            match.setExplanationJson("{\"summary\": \"" + summary + "\"}");
        }

        return match;
    }

    private int calculateTechnicalScore(Startup startup, Tender tender, List<TenderRequirement> requirements, List<String> strengths) {
        if (startup.getCapabilities() == null || startup.getCapabilities().isEmpty()) return 30;

        String tenderText = (tender.getTitle() + " " + tender.getCategory() + " " + tender.getDepartment()).toLowerCase();
        int matchedCaps = 0;

        for (StartupCapability cap : startup.getCapabilities()) {
            String capName = cap.getName().toLowerCase();
            String[] tokens = capName.split("[^a-z0-9]+");
            boolean capMatch = false;
            for (String token : tokens) {
                if (token.length() > 3 && tenderText.contains(token)) {
                    capMatch = true;
                    break;
                }
            }
            if (capMatch) {
                matchedCaps++;
                strengths.add("Core capability aligns with tender scope: " + cap.getName() + " (" + cap.getProficiencyLevel() + ")");
            }
        }

        int score = 50 + (matchedCaps * 15);
        return Math.min(100, Math.max(20, score));
    }

    private int calculateSectorScore(Startup startup, Tender tender) {
        if (startup.getPrimarySector() == null || tender.getCategory() == null) return 50;

        String sSector = startup.getPrimarySector().toLowerCase();
        String tCat = tender.getCategory().toLowerCase();

        if (sSector.contains("defense") && tCat.contains("defence")) return 95;
        if (sSector.contains("ai") && (tCat.contains("ai") || tCat.contains("surveillance"))) return 92;
        if (sSector.contains("health") && tCat.contains("health")) return 90;
        if (sSector.contains("civil") && tCat.contains("civil")) return 90;

        String[] sWords = sSector.split("[^a-z0-9]+");
        for (String w : sWords) {
            if (w.length() > 3 && tCat.contains(w)) return 85;
        }

        return 35; // Different sector
    }

    private int calculateDocumentReadiness(Startup startup, List<TenderRequirement> requirements) {
        List<StartupDocument> docs = startup.getDocuments();
        if (docs == null || docs.isEmpty()) return 20;

        int score = 40;
        boolean hasPan = docs.stream().anyMatch(d -> "PAN_CARD".equalsIgnoreCase(d.getDocType()));
        boolean hasGst = docs.stream().anyMatch(d -> "GST_CERTIFICATE".equalsIgnoreCase(d.getDocType()));
        boolean hasDpiit = docs.stream().anyMatch(d -> "DPIIT_CERTIFICATE".equalsIgnoreCase(d.getDocType()));

        if (hasPan) score += 20;
        if (hasGst) score += 20;
        if (hasDpiit) score += 20;

        return Math.min(100, score);
    }

    private int calculateFitScore(Startup startup, Tender tender) {
        int fit = 75;
        if (Boolean.TRUE.equals(startup.getDpiitRecognized())) fit += 15;
        if (tender.getEmdInr() != null && tender.getEmdInr().doubleValue() > 0) fit += 10; // startup benefits from EMD exemption
        return Math.min(100, fit);
    }

    private String generateSummary(int overallScore, Startup startup, Tender tender, int passCount, int failCount, int reviewCount) {
        if (overallScore >= 80) {
            return "Strong Opportunity Match (" + overallScore + "%). Your startup capabilities in " +
                    (startup.getPrimarySector() != null ? startup.getPrimarySector() : "core domains") +
                    " strongly align with tender specifications. " + passCount + " criteria fully verified.";
        } else if (overallScore >= 50) {
            return "Moderate Fit (" + overallScore + "%). While domain capabilities partially match, " +
                    (failCount > 0 ? failCount + " criteria failed and " : "") +
                    reviewCount + " clauses require human review or additional certifications.";
        } else {
            return "Low Fit (" + overallScore + "%). Tender requirements significantly deviate from startup registered domain, experience threshold, or financial turnover scale.";
        }
    }
}
