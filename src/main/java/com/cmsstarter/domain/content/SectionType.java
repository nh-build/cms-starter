package com.cmsstarter.domain.content;

public enum SectionType {
    HERO("메인 배너"), FEATURED("추천 상품"), NEW("신상품"), BEST("베스트"), CATEGORY("카테고리"), EVENT("이벤트");

    private final String label;

    SectionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
