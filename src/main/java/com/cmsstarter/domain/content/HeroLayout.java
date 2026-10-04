package com.cmsstarter.domain.content;

/** 메인 히어로 레이아웃 규칙 (화면 구성과 무관한 순수 로직). */
public final class HeroLayout {

    public static final String FULL = "FULL";

    private HeroLayout() {
    }

    /**
     * 헤더를 히어로 이미지 위에 투명하게 겹칠지 여부.
     * 풀 이미지형이고, 노출 중인 배너가 있고, 메인 배너 섹션이 맨 위일 때만 겹친다.
     * (히어로가 아래로 내려가 있으면 헤더가 엉뚱한 콘텐츠 위에 겹치므로 일반 헤더를 쓴다.)
     */
    public static boolean overlayHeader(String heroPreset, SectionType firstVisibleSection, boolean hasBanner) {
        return FULL.equals(heroPreset) && hasBanner && firstVisibleSection == SectionType.HERO;
    }
}
