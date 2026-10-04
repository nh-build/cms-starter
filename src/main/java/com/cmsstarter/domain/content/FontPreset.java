package com.cmsstarter.domain.content;

/**
 * 선택 가능한 한국어 웹폰트 프리셋 (모두 오픈 라이선스). 파일 업로드는 제공하지 않는다.
 * familyStack 끝에는 시스템 폰트를 둬서 CDN 로드에 실패해도 기본 폰트로 표시된다.
 */
public enum FontPreset {

    PRETENDARD("Pretendard", "'Pretendard'",
            "https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css"),
    NANUM_GOTHIC("나눔고딕", "'Nanum Gothic'",
            "https://fonts.googleapis.com/css2?family=Nanum+Gothic:wght@400;700;800&display=swap"),
    NANUM_SQUARE_ROUND("나눔스퀘어라운드", "'Nanum Square Round'",
            "https://cdn.jsdelivr.net/gh/fonts-archive/NanumSquareRound/NanumSquareRound.css"),
    NOTO_SANS_KR("본고딕 (Noto Sans KR)", "'Noto Sans KR'",
            "https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap"),
    IBM_PLEX_SANS_KR("IBM Plex Sans KR", "'IBM Plex Sans KR'",
            "https://fonts.googleapis.com/css2?family=IBM+Plex+Sans+KR:wght@400;500;700&display=swap");

    public static final FontPreset DEFAULT = PRETENDARD;

    private static final String FALLBACK =
            "-apple-system, BlinkMacSystemFont, 'Apple SD Gothic Neo', 'Malgun Gothic', 'Segoe UI', sans-serif";

    private final String label;
    private final String family;
    private final String cssUrl;

    FontPreset(String label, String family, String cssUrl) {
        this.label = label;
        this.family = family;
        this.cssUrl = cssUrl;
    }

    public String getLabel() {
        return label;
    }

    public String getCssUrl() {
        return cssUrl;
    }

    /** CSS font-family 값 (선택 폰트 + 시스템 폰트 폴백) */
    public String getFamilyStack() {
        return family + ", " + FALLBACK;
    }

    /** 알 수 없는 값이면 기본 폰트. */
    public static FontPreset fromKey(String key) {
        if (key != null) {
            for (FontPreset p : values()) {
                if (p.name().equalsIgnoreCase(key.trim())) {
                    return p;
                }
            }
        }
        return DEFAULT;
    }
}
