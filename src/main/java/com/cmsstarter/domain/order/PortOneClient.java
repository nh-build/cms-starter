package com.cmsstarter.domain.order;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.cmsstarter.config.PortOneProperties;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

/** 포트원(아임포트) V1 REST 결제 검증. verify-enabled=false 이면 검증을 건너뛴다(개발 전용). */
@Component
@RequiredArgsConstructor
public class PortOneClient {

    private static final String BASE = "https://api.iamport.kr";

    private final PortOneProperties props;
    private final RestClient rest = RestClient.create();

    public void verify(String impUid, String merchantUid, int expectedAmount) {
        if (!props.isVerifyEnabled()) {
            return;
        }
        if (props.getApiKey().isBlank() || props.getApiSecret().isBlank()) {
            throw new IllegalStateException("PORTONE_API_KEY / PORTONE_API_SECRET 이 설정되지 않았습니다.");
        }
        JsonNode token = rest.post().uri(BASE + "/users/getToken")
                .body(Map.of("imp_key", props.getApiKey(), "imp_secret", props.getApiSecret()))
                .retrieve().body(JsonNode.class);
        String access = token == null ? null : token.path("response").path("access_token").asText(null);
        if (access == null) {
            throw new IllegalStateException("포트원 토큰 발급에 실패했습니다.");
        }
        JsonNode payment = rest.get().uri(BASE + "/payments/{id}", impUid)
                .header("Authorization", access)
                .retrieve().body(JsonNode.class);
        JsonNode r = payment == null ? null : payment.path("response");
        if (r == null || r.isMissingNode() || r.isNull()) {
            throw new IllegalStateException("결제 정보를 조회할 수 없습니다.");
        }
        if (!"paid".equals(r.path("status").asText())
                || r.path("amount").asInt(-1) != expectedAmount
                || !merchantUid.equals(r.path("merchant_uid").asText())) {
            throw new IllegalStateException("결제 검증에 실패했습니다.");
        }
    }
}
