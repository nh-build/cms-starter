package com.cmsstarter.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ColorUtilTest {

    @Test
    void validatesHexColors() {
        assertThat(ColorUtil.isHex("#4E90C4")).isTrue();
        assertThat(ColorUtil.isHex("#abc")).isTrue();
        assertThat(ColorUtil.isHex("4E90C4")).isFalse();
        assertThat(ColorUtil.isHex("#12345")).isFalse();
        assertThat(ColorUtil.isHex("red;}</style>")).isFalse();
        assertThat(ColorUtil.isHex(null)).isFalse();
    }

    @Test
    void normalizesToUppercaseSixDigits() {
        assertThat(ColorUtil.normalize("#abc")).isEqualTo("#AABBCC");
        assertThat(ColorUtil.normalize(" #4e90c4 ")).isEqualTo("#4E90C4");
        assertThat(ColorUtil.normalize("nope")).isNull();
    }

    @Test
    void luminanceOfBlackAndWhite() {
        assertThat(ColorUtil.luminance("#000000")).isEqualTo(0.0);
        assertThat(ColorUtil.luminance("#FFFFFF")).isEqualTo(1.0, org.assertj.core.data.Offset.offset(0.001));
        assertThatThrownBy(() -> ColorUtil.luminance("x")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void picksReadableTextColorOnBrandBackground() {
        assertThat(ColorUtil.onColor("#4E90C4")).isEqualTo(ColorUtil.WHITE); // 기본 포인트색은 목업대로 흰 글자
        assertThat(ColorUtil.onColor("#111111")).isEqualTo(ColorUtil.WHITE);
        assertThat(ColorUtil.onColor("#FFE066")).isEqualTo(ColorUtil.INK);
        assertThat(ColorUtil.onColor("#FFFFFF")).isEqualTo(ColorUtil.INK);
    }

    @Test
    void rejectsTooDarkPageBackgrounds() {
        assertThat(ColorUtil.isLightEnoughForBackground("#F7FBFE")).isTrue();
        assertThat(ColorUtil.isLightEnoughForBackground("#FFFFFF")).isTrue();
        assertThat(ColorUtil.isLightEnoughForBackground("#222222")).isFalse();
        assertThat(ColorUtil.isLightEnoughForBackground("#4E90C4")).isFalse();
    }
}
