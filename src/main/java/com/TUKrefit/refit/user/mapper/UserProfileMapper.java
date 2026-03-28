package com.TUKrefit.refit.user.mapper;

import com.TUKrefit.refit.common.util.TimeUtil;
import com.TUKrefit.refit.user.dto.UserProfileResponse;
import com.TUKrefit.refit.user.dto.UserProfileUpsertRequest;
import com.TUKrefit.refit.user.entity.User;
import com.TUKrefit.refit.user.entity.UserProfile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class UserProfileMapper {

    private static final ObjectMapper OM = new ObjectMapper();

    private UserProfileMapper() {}

    // 프로필이 없을 때 신규 생성
    public static UserProfile toNew(User user, UserProfileUpsertRequest req) {
        long now = TimeUtil.nowMs();
        return UserProfile.builder()
                .userId(user.getUserId())
                .user(user)
                .heightCm(req.getHeightCm())
                .weightKg(req.getWeightKg())
                .dominantHand(normalizeDominantHand(req.getDominantHand()))
                .diagnosisTags(toJson(req.getDiagnosisTags()))
                .painBaseline0to10(req.getPainBaseline0to10())
                .notes(req.getNotes())
                .updatedAtMs(now)
                .build();
    }

    // 기존 엔티티 수정
    public static void apply(UserProfile entity, UserProfileUpsertRequest req) {
        long now = TimeUtil.nowMs();
        entity.setHeightCm(req.getHeightCm());
        entity.setWeightKg(req.getWeightKg());
        entity.setDominantHand(normalizeDominantHand(req.getDominantHand()));
        entity.setDiagnosisTags(toJson(req.getDiagnosisTags()));
        entity.setPainBaseline0to10(req.getPainBaseline0to10());
        entity.setNotes(req.getNotes());
        entity.setUpdatedAtMs(now);
    }

    public static UserProfileResponse toResponse(UserProfile entity) {
        return UserProfileResponse.builder()
                .userId(entity.getUserId())
                .heightCm(entity.getHeightCm())
                .weightKg(entity.getWeightKg())
                .dominantHand(entity.getDominantHand())
                .diagnosisTags(fromJson(entity.getDiagnosisTags()))
                .painBaseline0to10(entity.getPainBaseline0to10())
                .notes(entity.getNotes())
                .updatedAtMs(entity.getUpdatedAtMs())
                .build();
    }

    private static String normalizeDominantHand(String v) {
        if (v == null || v.isBlank()) return null;
        String x = v.trim().toUpperCase();
        if (!x.equals("L") && !x.equals("R")) return null;
        return x;
    }

    private static String toJson(List<String> tags) {
        try {
            if (tags == null) return "[]";
            return OM.writeValueAsString(tags);
        } catch (Exception e) {
            // 비정상 데이터는 빈 배열로 저장
            return "[]";
        }
    }

    private static List<String> fromJson(String json) {
        try {
            if (json == null || json.isBlank()) return Collections.emptyList();
            return OM.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // 파싱 실패 시 응답은 빈 배열로 고정
            return Collections.emptyList();
        }
    }
}
