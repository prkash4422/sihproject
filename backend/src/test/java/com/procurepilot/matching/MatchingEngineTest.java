package com.procurepilot.matching;

import com.procurepilot.eligibility.DeterministicEligibilityEngine;
import com.procurepilot.eligibility.EligibilityEvaluation;
import com.procurepilot.eligibility.EligibilityResult;
import com.procurepilot.requirement.RequirementType;
import com.procurepilot.requirement.TenderRequirement;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupCapability;
import com.procurepilot.startup.StartupCertification;
import com.procurepilot.startup.StartupDocument;
import com.procurepilot.tender.Tender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchingEngineTest {

    @Mock
    private DeterministicEligibilityEngine eligibilityEngine;

    private MatchingEngine matchingEngine;

    @BeforeEach
    void setUp() {
        matchingEngine = new MatchingEngine(eligibilityEngine);
    }

    @Test
    void testCalculateMatch_HighMatchProducesTransparentExplanation() {
        Startup startup = new Startup();
        startup.setPrimarySector("Defense, AI & Computer Vision");
        startup.setDpiitRecognized(true);

        StartupCapability cap = new StartupCapability();
        cap.setName("Edge AI Video Analytics");
        cap.setProficiencyLevel("EXPERT");
        startup.setCapabilities(Collections.singletonList(cap));

        StartupDocument doc = new StartupDocument();
        doc.setDocType("PAN_CARD");
        startup.setDocuments(Collections.singletonList(doc));

        Tender tender = new Tender();
        tender.setTitle("Procurement of Edge AI Video Surveillance");
        tender.setCategory("Defence & Surveillance AI");
        tender.setEmdInr(new BigDecimal("300000"));

        TenderRequirement req1 = new TenderRequirement();
        req1.setType(RequirementType.TURNOVER);
        req1.setNormalizedValue("10000000");

        EligibilityEvaluation eval1 = new EligibilityEvaluation();
        eval1.setResult(EligibilityResult.PASS);
        eval1.setStartupValue("₹1.80 Cr");

        when(eligibilityEngine.evaluateRequirement(any(Startup.class), any(TenderRequirement.class)))
                .thenReturn(eval1);

        StartupTenderMatch match = matchingEngine.calculateMatch(startup, tender, Collections.singletonList(req1));

        assertNotNull(match);
        assertTrue(match.getOverallScore() >= 75);
        assertTrue(match.getTechnicalScore() >= 60);
        assertTrue(match.getSectorScore() >= 90);
        assertNotNull(match.getExplanationJson());
        assertTrue(match.getExplanationJson().contains("Strong Opportunity Match") || match.getExplanationJson().contains("summary"));
    }
}
