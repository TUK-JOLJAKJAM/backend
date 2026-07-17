package com.TUKrefit.refit.analysis.service;

import com.TUKrefit.refit.analysis.client.AiAnalysisClient;
import com.TUKrefit.refit.analysis.entity.AnalysisResult;
import com.TUKrefit.refit.analysis.repository.AnalysisResultRepository;
import com.TUKrefit.refit.game.dto.GameHistoryDetailResponse;
import com.TUKrefit.refit.game.service.GameHistoryService;
import com.TUKrefit.refit.user.dto.UserProfileResponse;
import com.TUKrefit.refit.user.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalysisServiceTest {
    private GameHistoryService gameHistoryService;
    private UserProfileService userProfileService;
    private AnalysisResultRepository repository;
    private AiAnalysisClient client;
    private AnalysisService service;

    @BeforeEach
    void setUp() {
        gameHistoryService = mock(GameHistoryService.class);
        userProfileService = mock(UserProfileService.class);
        repository = mock(AnalysisResultRepository.class);
        client = mock(AiAnalysisClient.class);
        service = new AnalysisService(
                gameHistoryService,
                userProfileService,
                repository,
                client
        );
    }

    @Test
    void analyzesHistoryWithProfileAndPersistsVersionedResult() {
        GameHistoryDetailResponse history = GameHistoryDetailResponse.builder()
                .historyId("history-1")
                .userId("user-1")
                .schemaVersion("2.0")
                .gameId("Adventure")
                .build();
        UserProfileResponse profile = UserProfileResponse.builder()
                .userId("user-1")
                .heightCm(170f)
                .painBaseline0to10(2)
                .build();
        when(gameHistoryService.detail("user-1", "history-1")).thenReturn(history);
        when(userProfileService.get("user-1")).thenReturn(profile);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("analysis_id", "analysis-1");
        result.put("analysis_version", "rules-2.0.0");
        result.put("schema_version", "2.0");
        result.put("score", 82);
        result.put("safety_status", "SAFE");
        result.put("difficulty_recommend", "MAINTAIN");
        result.put("analyzed_at_ms", 1_784_263_867_950L);
        result.put("data_quality", Map.of("status", "GOOD", "completeness", 1.0));
        when(client.analyze(anyMap())).thenReturn(result);

        Map<String, Object> response = service.analyzeAndSave("user-1", "history-1");

        assertSame(result, response);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> inputCaptor = ArgumentCaptor.forClass(Map.class);
        verify(client).analyze(inputCaptor.capture());
        assertEquals("history-1", inputCaptor.getValue().get("historyId"));
        UserProfileResponse forwardedProfile = new ObjectMapper().convertValue(
                inputCaptor.getValue().get("profile"),
                UserProfileResponse.class
        );
        assertEquals("user-1", forwardedProfile.getUserId());
        assertEquals(170f, forwardedProfile.getHeightCm());
        assertEquals(2, forwardedProfile.getPainBaseline0to10());

        ArgumentCaptor<AnalysisResult> entityCaptor = ArgumentCaptor.forClass(AnalysisResult.class);
        verify(repository).save(entityCaptor.capture());
        AnalysisResult saved = entityCaptor.getValue();
        assertEquals("analysis-1", saved.getAnalysisId());
        assertEquals("rules-2.0.0", saved.getAnalysisVersion());
        assertEquals("user-1", saved.getUserId());
        assertEquals(64, saved.getInputHash().length());
        assertTrue(saved.getResultJson().contains("\"score\":82"));
    }
}
