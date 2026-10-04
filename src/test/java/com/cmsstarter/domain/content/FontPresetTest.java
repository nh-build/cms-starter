package com.cmsstarter.domain.content;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FontPresetTest {

    @Test
    void providesFiveKoreanPresets() {
        assertThat(FontPreset.values()).hasSize(5);
        for (FontPreset p : FontPreset.values()) {
            assertThat(p.getCssUrl()).startsWith("https://");
            assertThat(p.getLabel()).isNotBlank();
        }
    }

    @Test
    void everyStackEndsWithSystemFallback() {
        for (FontPreset p : FontPreset.values()) {
            assertThat(p.getFamilyStack()).endsWith("sans-serif").contains("'Malgun Gothic'");
        }
    }

    @Test
    void unknownKeysFallBackToDefault() {
        assertThat(FontPreset.fromKey("noto_sans_kr")).isEqualTo(FontPreset.NOTO_SANS_KR);
        assertThat(FontPreset.fromKey("comic-sans")).isEqualTo(FontPreset.PRETENDARD);
        assertThat(FontPreset.fromKey(null)).isEqualTo(FontPreset.PRETENDARD);
    }
}
