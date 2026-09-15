package com.procurepilot.eligibility;

import com.procurepilot.requirement.RequirementType;
import com.procurepilot.requirement.TenderRequirement;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupCapability;
import com.procurepilot.startup.StartupCertification;
import com.procurepilot.startup.StartupDocument;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Component
public class DeterministicEligibilityEngine {

    private final EligibilityRuleRepository ruleRepository;

    public DeterministicEligibilityEngine(EligibilityRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public EligibilityEvaluation evaluateRequirement(Startup startup, TenderRequirement req) {
        EligibilityEvaluation eval = new EligibilityEvaluation();
        eval.setRequirement(req);
        eval.setOperator(req.getOperator() != null ? req.getOperator().name() : "GTE");
        eval.setSourcePage(req.getSourcePage());
        eval.setSourceSection(req.getSourceSection());
        eval.setTenderValue(req.getNormalizedValue() != null ? req.getNormalizedValue() : req.getRequirementText());

        RequirementType type = req.getType();

        switch (type) {
            case TURNOVER:
                evaluateTurnover(startup, req, eval);
                break;
            case EXPERIENCE:
                evaluateExperience(startup, req, eval);
                break;
            case CERTIFICATION:
                evaluateCertification(startup, req, eval);
                break;
            case TECHNICAL:
            case PRODUCT:
            case SERVICE:
                evaluateTechnical(startup, req, eval);
                break;
            case LEGAL:
                evaluateLegal(startup, req, eval);
                break;
            case DOCUMENT:
                evaluateDocument(startup, req, eval);
                break;
            default:
                eval.setStartupValue("N/A");
                eval.setResult(EligibilityResult.NOT_APPLICABLE);
                eval.setReason("General tender clause. Does not require specific startup credential evaluation.");
                break;
        }

        return eval;
    }

    private void evaluateTurnover(Startup startup, TenderRequirement req, EligibilityEvaluation eval) {
        BigDecimal requiredTurnover = parseAmount(req.getNormalizedValue());
        BigDecimal startupTurnover = startup.getAnnualTurnoverInr() != null ? startup.getAnnualTurnoverInr() : BigDecimal.ZERO;

        eval.setStartupValue("INR " + formatInr(startupTurnover));

        if (startupTurnover.compareTo(BigDecimal.ZERO) == 0) {
            if (Boolean.TRUE.equals(startup.getDpiitRecognized())) {
                Optional<EligibilityRule> gfrRule = ruleRepository.findByRuleCode("GFR-161-IV");
                eval.setResult(EligibilityResult.PASS);
                eval.setReason("Turnover data missing, but DPIIT Recognized Startup is exempted from prior turnover criteria as per Rule 161(iv) of General Financial Rules (GFR) 2017.");
                gfrRule.ifPresent(eval::setAppliedRelaxationRule);
            } else {
                eval.setResult(EligibilityResult.MISSING_DATA);
                eval.setReason("Annual turnover information has not been entered in the startup profile.");
            }
            return;
        }

        if (startupTurnover.compareTo(requiredTurnover) >= 0) {
            eval.setResult(EligibilityResult.PASS);
            eval.setReason("Startup annual turnover of INR " + formatInr(startupTurnover) + " meets or exceeds the required threshold of INR " + formatInr(requiredTurnover) + ".");
        } else {
            // Below threshold -> Check DPIIT Exemption
            if (Boolean.TRUE.equals(startup.getDpiitRecognized())) {
                Optional<EligibilityRule> gfrRule = ruleRepository.findByRuleCode("GFR-161-IV");
                eval.setResult(EligibilityResult.PASS);
                eval.setReason("Turnover (INR " + formatInr(startupTurnover) + ") is below tender threshold (INR " + formatInr(requiredTurnover) + "), but startup qualifies for 100% turnover exemption under GFR Rule 161(iv) & DPIIT Notification No. 5(4)/2017-BE-I.");
                gfrRule.ifPresent(eval::setAppliedRelaxationRule);
            } else {
                eval.setResult(EligibilityResult.FAIL);
                eval.setReason("Startup annual turnover of INR " + formatInr(startupTurnover) + " is below the required INR " + formatInr(requiredTurnover) + ". DPIIT startup recognition not registered to claim GFR relaxation.");
            }
        }
    }

    private void evaluateExperience(Startup startup, TenderRequirement req, EligibilityEvaluation eval) {
        int requiredYears = parseInteger(req.getNormalizedValue(), 2);
        int startupYears = 0;

        if (startup.getIncorporationDate() != null) {
            startupYears = Period.between(startup.getIncorporationDate(), LocalDate.now()).getYears();
        }

        eval.setStartupValue(startupYears + " Years (Incorporated: " + (startup.getIncorporationDate() != null ? startup.getIncorporationDate() : "Unknown") + ")");

        if (startup.getIncorporationDate() == null) {
            if (Boolean.TRUE.equals(startup.getDpiitRecognized())) {
                Optional<EligibilityRule> gfrRule = ruleRepository.findByRuleCode("GFR-161-IV");
                eval.setResult(EligibilityResult.PASS);
                eval.setReason("Incorporation date missing, but DPIIT Recognized Startup is eligible for prior experience relaxation under GFR 161(iv) subject to technical capability validation.");
                gfrRule.ifPresent(eval::setAppliedRelaxationRule);
            } else {
                eval.setResult(EligibilityResult.MISSING_DATA);
                eval.setReason("Incorporation date is missing from company profile.");
            }
            return;
        }

        if (startupYears >= requiredYears) {
            eval.setResult(EligibilityResult.PASS);
            eval.setReason("Startup operational experience of " + startupYears + " years meets the tender requirement of " + requiredYears + " years.");
        } else {
            // Below experience threshold -> Check DPIIT Exemption
            if (Boolean.TRUE.equals(startup.getDpiitRecognized())) {
                Optional<EligibilityRule> gfrRule = ruleRepository.findByRuleCode("GFR-161-IV");
                eval.setResult(EligibilityResult.PASS);
                eval.setReason("Startup operational experience (" + startupYears + " yrs) is under the tender requirement (" + requiredYears + " yrs), but eligible for prior experience relaxation under GFR Rule 161(iv). Ensure prototype test report is attached.");
                gfrRule.ifPresent(eval::setAppliedRelaxationRule);
            } else {
                eval.setResult(EligibilityResult.FAIL);
                eval.setReason("Startup operational experience (" + startupYears + " yrs) is below the required " + requiredYears + " years.");
            }
        }
    }

    private void evaluateCertification(Startup startup, TenderRequirement req, EligibilityEvaluation eval) {
        String reqCert = req.getNormalizedValue() != null ? req.getNormalizedValue().toLowerCase() : req.getRequirementText().toLowerCase();
        List<StartupCertification> certs = startup.getCertifications();

        boolean matched = false;
        String matchedCertName = null;

        if (certs != null) {
            for (StartupCertification cert : certs) {
                String certType = cert.getCertType().toLowerCase();
                if (certType.contains(reqCert) || reqCert.contains(certType) ||
                    (reqCert.contains("iso 9001") && certType.contains("iso 9001")) ||
                    (reqCert.contains("iso 27001") && certType.contains("iso 27001")) ||
                    (reqCert.contains("cmmi") && certType.contains("cmmi")) ||
                    (reqCert.contains("dpiit") && Boolean.TRUE.equals(startup.getDpiitRecognized())) ||
                    (reqCert.contains("udyam") && startup.getUdyamNumber() != null)) {
                    matched = true;
                    matchedCertName = cert.getCertType() + " (" + (cert.getCertNumber() != null ? cert.getCertNumber() : "Active") + ")";
                    break;
                }
            }
        }

        if (matched) {
            eval.setStartupValue(matchedCertName);
            eval.setResult(EligibilityResult.PASS);
            eval.setReason("Required certification matches active verified credentials: " + matchedCertName + ".");
        } else {
            eval.setStartupValue("Not Found");
            if (Boolean.TRUE.equals(req.getMandatory())) {
                eval.setResult(EligibilityResult.FAIL);
                eval.setReason("Mandatory certification '" + req.getNormalizedValue() + "' is not present in startup profile. Upload certificate to fulfill compliance.");
            } else {
                eval.setResult(EligibilityResult.NEEDS_REVIEW);
                eval.setReason("Preferred certification '" + req.getNormalizedValue() + "' is absent. Check if equivalent standard is acceptable.");
            }
        }
    }

    private void evaluateTechnical(Startup startup, TenderRequirement req, EligibilityEvaluation eval) {
        String reqText = (req.getNormalizedValue() != null ? req.getNormalizedValue() : req.getRequirementText()).toLowerCase();
        List<StartupCapability> capabilities = startup.getCapabilities();

        boolean matched = false;
        StringBuilder matchedCaps = new StringBuilder();

        if (capabilities != null) {
            for (StartupCapability cap : capabilities) {
                String capText = (cap.getName() + " " + (cap.getDescription() != null ? cap.getDescription() : "")).toLowerCase();
                String[] words = reqText.split("[^a-zA-Z0-9]+");
                int matchCount = 0;
                for (String word : words) {
                    if (word.length() > 3 && capText.contains(word)) {
                        matchCount++;
                    }
                }
                if (matchCount >= 1 || capText.contains(reqText) || reqText.contains(cap.getName().toLowerCase())) {
                    matched = true;
                    if (matchedCaps.length() > 0) matchedCaps.append(", ");
                    matchedCaps.append(cap.getName()).append(" (").append(cap.getProficiencyLevel()).append(")");
                }
            }
        }

        if (matched) {
            eval.setStartupValue(matchedCaps.toString());
            eval.setResult(EligibilityResult.PASS);
            eval.setReason("Technical capabilities directly align with tender specification: " + matchedCaps.toString() + ".");
        } else {
            eval.setStartupValue("Partial Match");
            eval.setResult(EligibilityResult.NEEDS_REVIEW);
            eval.setReason("Technical specification requires domain review against current technical capability portfolio.");
        }
    }

    private void evaluateLegal(Startup startup, TenderRequirement req, EligibilityEvaluation eval) {
        String reqText = req.getRequirementText().toLowerCase();
        boolean hasPan = false;
        boolean hasGst = false;

        if (startup.getDocuments() != null) {
            for (StartupDocument doc : startup.getDocuments()) {
                if ("PAN_CARD".equalsIgnoreCase(doc.getDocType()) || doc.getFileName().toLowerCase().contains("pan")) hasPan = true;
                if ("GST_CERTIFICATE".equalsIgnoreCase(doc.getDocType()) || doc.getFileName().toLowerCase().contains("gst")) hasGst = true;
            }
        }

        eval.setStartupValue("PAN: " + (hasPan ? "Verified" : "Missing") + ", GST: " + (hasGst ? "Verified" : "Missing"));

        if (hasPan && hasGst) {
            eval.setResult(EligibilityResult.PASS);
            eval.setReason("Statutory registrations (PAN & GST) are uploaded and verified.");
        } else if (hasPan || hasGst) {
            eval.setResult(EligibilityResult.NEEDS_REVIEW);
            eval.setReason("Partial statutory documentation uploaded. Ensure both PAN and GSTIN certificates are attached.");
        } else {
            eval.setResult(EligibilityResult.MISSING_DATA);
            eval.setReason("PAN and GST registration certificates must be uploaded to satisfy mandatory statutory requirement.");
        }
    }

    private void evaluateDocument(Startup startup, TenderRequirement req, EligibilityEvaluation eval) {
        String reqText = req.getRequirementText().toLowerCase();
        if (reqText.contains("dpiit") || reqText.contains("startup")) {
            if (Boolean.TRUE.equals(startup.getDpiitRecognized())) {
                eval.setStartupValue("DPIIT Recognized (" + (startup.getDpiitNumber() != null ? startup.getDpiitNumber() : "Verified") + ")");
                eval.setResult(EligibilityResult.PASS);
                eval.setReason("Valid DPIIT Startup Recognition credential is on file.");
                Optional<EligibilityRule> emdRule = ruleRepository.findByRuleCode("MSE-EMD-EXEMPTION");
                emdRule.ifPresent(eval::setAppliedRelaxationRule);
            } else {
                eval.setStartupValue("Not Recognized");
                eval.setResult(EligibilityResult.NEEDS_REVIEW);
                eval.setReason("Startup is not DPIIT recognized. Exemption benefit cannot be claimed.");
            }
        } else {
            eval.setStartupValue("Refer Compliance Workspace");
            eval.setResult(EligibilityResult.PASS);
            eval.setReason("Document requirement mapped to Dynamic Compliance Checklist.");
        }
    }

    private BigDecimal parseAmount(String val) {
        if (val == null) return BigDecimal.ZERO;
        try {
            return new BigDecimal(val.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private int parseInteger(String val, int defaultVal) {
        if (val == null) return defaultVal;
        try {
            return Integer.parseInt(val.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private String formatInr(BigDecimal amount) {
        if (amount == null) return "0.00";
        BigDecimal crore = new BigDecimal("10000000");
        BigDecimal lakh = new BigDecimal("100000");

        if (amount.compareTo(crore) >= 0) {
            return amount.divide(crore, 2, java.math.RoundingMode.HALF_UP) + " Crore";
        } else if (amount.compareTo(lakh) >= 0) {
            return amount.divide(lakh, 2, java.math.RoundingMode.HALF_UP) + " Lakh";
        } else {
            return amount.toPlainString();
        }
    }
}
