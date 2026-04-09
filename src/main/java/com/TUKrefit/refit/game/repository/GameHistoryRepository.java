package com.TUKrefit.refit.game.repository;

import com.TUKrefit.refit.game.entity.GameHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GameHistoryRepository extends JpaRepository<GameHistory, String> {

    Optional<GameHistory> findByHistoryIdAndUserId(String historyId, String userId);

    @Query("""
            select g from GameHistory g
            where g.userId = :userId
              and (:gameId is null or g.gameId = :gameId)
              and (:fromMs is null or g.endedAtMs >= :fromMs)
              and (:toMs is null or g.endedAtMs <= :toMs)
            order by g.endedAtMs desc
            """)
    Page<GameHistory> search(
            @Param("userId") String userId,
            @Param("gameId") String gameId,
            @Param("fromMs") Long fromMs,
            @Param("toMs") Long toMs,
            Pageable pageable
    );
}
