package com.procurepilot.eligibility;

import com.procurepilot.requirement.RequirementOperator;
import com.procurepilot.requirement.RequirementType;
import com.procurepilot.requirement.TenderRequirement;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupCertification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeterministicEligibilityEngineTest {

    @Mock
    private EligibilityRuleRepository ruleRepository;

    private DeterministicEligibilityEngine engine;

    @BeforeEach
    void setUp() {
        engine = new DeterministicEligibilityEngine(ruleRepository);
    }

    @Test
    void testTurnoverExceedsThreshold_Passes() {
        Startup startup = new Startup();
        startup.setAnnualTurnoverInr(new BigDecimal("18000000")); // 1.8 Cr

        TenderRequirement req = new TenderRequirement();
        req.setType(RequirementType.TURNOVER);
        req.setNormalizedValue("10000000"); // 1.0 Cr
        req.setOperator(RequirementOperator.GTE);

        EligibilityEvaluation eval = engine.evaluateRequirement(startup, req);

        assertEquals(EligibilityResult.PASS, eval.getResult());
        assertTrue(eval.getReason().contains("meets or exceeds"));
    }

    @Test
    void testTurnoverBelowThreshold_WithoutDpiit_Fails() {
        Startup startup = new Startup();
        startup.setAnnualTurnoverInr(new BigDecimal("5000000")); // 50 Lakhs
        startup.setDpiitRecognized(false);

        TenderRequirement req = new TenderRequirement();
        req.setType(RequirementType.TURNOVER);
        req.setNormalizedValue("10000000"); // 1.0 Cr
        req.setOperator(RequirementOperator.GTE);

        EligibilityEvaluation eval = engine.evaluateRequirement(startup, req);

        assertEquals(EligibilityResult.FAIL, eval.getResult());
        assertTrue(eval.getReason().contains("below the required"));
    }

    @Test
    void testTurnoverBelowThreshold_WithDpiitRecognition_PassesUnderGfr161iv() {
        Startup startup = new Startup();
        startup.setAnnualTurnoverInr(new BigDecimal("5000000")); // 50 Lakhs
        startup.setDpiitRecognized(true);
        startup.setDpiitNumber("DIPP98741");

        EligibilityRule gfrRule = new EligibilityRule();
        gfrRule.setRuleCode("GFR-161-IV");
        gfrRule.setName("Exemption from Prior Turnover and Experience");

        when(ruleRepository.findByRuleCode("GFR-161-IV")).thenReturn(Optional.of(gfrRule));

        TenderRequirement req = new TenderRequirement();
        req.setType(RequirementType.TURNOVER);
        req.setNormalizedValue("10000000"); // 1.0 Cr
        req.setOperator(RequirementOperator.GTE);

        EligibilityEvaluation eval = engine.evaluateRequirement(startup, req);

        assertEquals(EligibilityResult.PASS, eval.getResult());
        assertNotNull(eval.getAppliedRelaxationRule());
        assertEquals("GFR-161-IV", eval.getAppliedRelaxationRule().getRuleCode());
        assertTrue(eval.getReason().contains("GFR Rule 161(iv)"));
    }

    @Test
    void testMissingTurnoverData_WithoutDpiit_ReturnsMissingData() {
        Startup startup = new Startup();
        startup.setAnnualTurnoverInr(BigDecimal.ZERO);
        startup.setDpiitRecognized(false);

        TenderRequirement req = new TenderRequirement();
        req.setType(RequirementType.TURNOVER);
        req.setNormalizedValue("10000000");

        EligibilityEvaluation eval = engine.evaluateRequirement(startup, req);

        assertEquals(EligibilityResult.MISSING_DATA, eval.getResult());
    }

    @Test
    void testValidIsoCertificationMatch_Passes() {
        Startup startup = new Startup();
        StartupCertification cert = new StartupCertification();
        cert.setCertType("ISO 9001:2015");
        cert.setCertNumber("ISO-9912");
        startup.setCertifications(Collections.singletonList(cert));

        TenderRequirement req = new TenderRequirement();
        req.setType(RequirementType.CERTIFICATION);
        req.setNormalizedValue("ISO 9001:2015");
        req.setMandatory(true);

        EligibilityEvaluation eval = engine.evaluateRequirement(startup, req);

        assertEquals(EligibilityResult.PASS, eval.getResult());
        assertTrue(eval.getReason().contains("ISO 9001:2015"));
    }

    @Test
    void testMissingMandatoryCertification_Fails() {
        Startup startup = new Startup();
        startup.setCertifications(Collections.emptyList());

        TenderRequirement req = new TenderRequirement();
        req.setType(RequirementType.CERTIFICATION);
        req.setNormalizedValue("ISO 27001");
        req.setMandatory(true);

        EligibilityEvaluation eval = engine.evaluateRequirement(startup, req);

        assertEquals(EligibilityResult.FAIL, eval.getResult());
        assertTrue(eval.getReason().contains("Mandatory certification"));
    }
}
