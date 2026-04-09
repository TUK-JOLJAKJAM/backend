package com.TUKrefit.refit.game.service;

import com.TUKrefit.refit.auth.exception.AuthErrorCode;
import com.TUKrefit.refit.auth.exception.AuthException;
import com.TUKrefit.refit.game.dto.*;
import com.TUKrefit.refit.game.entity.GameHistory;
import com.TUKrefit.refit.game.exception.GameHistoryErrorCode;
import com.TUKrefit.refit.game.exception.GameHistoryException;
import com.TUKrefit.refit.game.mapper.GameHistoryMapper;
import com.TUKrefit.refit.game.repository.GameHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameHistoryService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final GameHistoryRepository gameHistoryRepository;

    @Transactional
    public GameHistoryCreateResponse create(String userId, GameHistoryCreateRequest req) {
        requireUser(userId);
        validateCreateRequest(req);

        GameHistory entity = GameHistoryMapper.toEntity(userId, req);
        gameHistoryRepository.save(entity);
        return GameHistoryMapper.toCreateResponse(entity);
    }

    @Transactional(readOnly = true)
    public GameHistoryListResponse list(String userId, String gameId, Long fromMs, Long toMs, Integer page, Integer size) {
        requireUser(userId);
        validateRange(fromMs, toMs);

        int safePage = (page == null || page < 0) ? 0 : page;
        int safeSize = (size == null || size <= 0) ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        Page<GameHistory> result = gameHistoryRepository.search(
                userId,
                normalize(gameId),
                fromMs,
                toMs,
                PageRequest.of(safePage, safeSize)
        );

        List<GameHistoryListItemResponse> items = result.getContent()
                .stream()
                .map(GameHistoryMapper::toListItem)
                .toList();

        return GameHistoryListResponse.builder()
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .hasNext(result.hasNext())
                .items(items)
                .build();
    }

    @Transactional(readOnly = true)
    public GameHistoryDetailResponse detail(String userId, String historyId) {
        requireUser(userId);

        GameHistory entity = gameHistoryRepository.findByHistoryIdAndUserId(historyId, userId)
                .orElseThrow(() -> new GameHistoryException(GameHistoryErrorCode.GAME_HISTORY_NOT_FOUND));

        return GameHistoryMapper.toDetail(entity);
    }

    private void requireUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
    }

    private void validateCreateRequest(GameHistoryCreateRequest req) {
        if (req.getEndedAtMs() < req.getStartedAtMs()) {
            throw new GameHistoryException(GameHistoryErrorCode.INVALID_GAME_HISTORY, "endedAtMs는 startedAtMs보다 작을 수 없습니다.");
        }

        if (req.getActionCount() != null && req.getSuccessCount() != null && req.getSuccessCount() > req.getActionCount()) {
            throw new GameHistoryException(GameHistoryErrorCode.INVALID_GAME_HISTORY, "successCount는 actionCount를 초과할 수 없습니다.");
        }

        if (req.getActionCount() != null && req.getFailCount() != null && req.getFailCount() > req.getActionCount()) {
            throw new GameHistoryException(GameHistoryErrorCode.INVALID_GAME_HISTORY, "failCount는 actionCount를 초과할 수 없습니다.");
        }
    }

    private void validateRange(Long fromMs, Long toMs) {
        if (fromMs != null && toMs != null && fromMs > toMs) {
            throw new GameHistoryException(GameHistoryErrorCode.INVALID_GAME_HISTORY, "fromMs는 toMs보다 클 수 없습니다.");
        }
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
