package com.cmsstarter.domain.content;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HeroLayoutTest {

    @Test
    void overlaysHeaderOnlyForFullPresetWithHeroOnTopAndABanner() {
        assertThat(HeroLayout.overlayHeader("FULL", SectionType.HERO, true)).isTrue();
    }

    @Test
    void noOverlayWhenHeroSectionIsNotFirst() {
        assertThat(HeroLayout.overlayHeader("FULL", SectionType.FEATURED, true)).isFalse();
        assertThat(HeroLayout.overlayHeader("FULL", null, true)).isFalse();
    }

    @Test
    void noOverlayWithoutBannerOrForOtherPresets() {
        assertThat(HeroLayout.overlayHeader("FULL", SectionType.HERO, false)).isFalse();
        assertThat(HeroLayout.overlayHeader("BANNER", SectionType.HERO, true)).isFalse();
        assertThat(HeroLayout.overlayHeader("SLIDE", SectionType.HERO, true)).isFalse();
        assertThat(HeroLayout.overlayHeader("NONE", SectionType.HERO, true)).isFalse();
    }
}
