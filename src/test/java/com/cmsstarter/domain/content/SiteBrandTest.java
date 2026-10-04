package com.cmsstarter.domain.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.cmsstarter.config.BrandProperties;

class SiteBrandTest {

    private final BrandProperties defaults = new BrandProperties();

    @Test
    void usesPropertiesDefaultsWhenNothingIsStored() {
        SiteBrand b = SiteBrand.resolve(defaults, Map.of());

        assertThat(b.getName()).isEqualTo("LUMO");
        assertThat(b.getPrimaryColor()).isEqualTo("#4E90C4");
        assertThat(b.getBackgroundColor()).isEqualTo("#F7FBFE");
        assertThat(b.getFontPreset()).isEqualTo(FontPreset.PRETENDARD);
        assertThat(b.hasLogo()).isFalse();
    }

    @Test
    void storedSettingsOverrideDefaults() {
        SiteBrand b = SiteBrand.resolve(defaults, Map.of(
                SiteSettingService.BRAND_NAME, "MOOD",
                SiteSettingService.BRAND_PRIMARY, "#112233",
                SiteSettingService.BRAND_BACKGROUND, "#fff",
                SiteSettingService.BRAND_LOGO, "/uploads/logo.png",
                SiteSettingService.FONT_PRESET, "NOTO_SANS_KR"));

        assertThat(b.getName()).isEqualTo("MOOD");
        assertThat(b.getPrimaryColor()).isEqualTo("#112233");
        assertThat(b.getBackgroundColor()).isEqualTo("#FFFFFF");
        assertThat(b.hasLogo()).isTrue();
        assertThat(b.getLogoPath()).isEqualTo("/uploads/logo.png");
        assertThat(b.getFontPreset()).isEqualTo(FontPreset.NOTO_SANS_KR);
        assertThat(b.getFontCssUrl()).contains("Noto+Sans+KR");
    }

    @Test
    void invalidStoredValuesAreIgnoredSoNothingCanBreakOutOfTheStyleTag() {
        SiteBrand b = SiteBrand.resolve(defaults, Map.of(
                SiteSettingService.BRAND_PRIMARY, "red;}</style><script>alert(1)</script>",
                SiteSettingService.BRAND_BACKGROUND, "url(javascript:x)",
                SiteSettingService.FONT_PRESET, "x;}</style>"));

        assertThat(b.getPrimaryColor()).isEqualTo("#4E90C4");
        assertThat(b.getBackgroundColor()).isEqualTo("#F7FBFE");
        assertThat(b.getFontPreset()).isEqualTo(FontPreset.PRETENDARD);
        assertThat(b.getCssVars()).doesNotContain("<").doesNotContain("script");
    }

    @Test
    void cssVarsExposeBrandBackgroundOnBrandAndFontFamily() {
        SiteBrand b = SiteBrand.resolve(defaults, Map.of(SiteSettingService.FONT_PRESET, "NANUM_GOTHIC"));

        assertThat(b.getCssVars())
                .contains("--brand:#4E90C4")
                .contains("--bg:#F7FBFE")
                .contains("--on-brand:#")
                .contains("--font-family:'Nanum Gothic'");
    }

    @Test
    void onBrandColorFollowsPrimaryLuminance() {
        SiteBrand dark = SiteBrand.resolve(defaults, Map.of(SiteSettingService.BRAND_PRIMARY, "#101820"));
        SiteBrand light = SiteBrand.resolve(defaults, Map.of(SiteSettingService.BRAND_PRIMARY, "#FFE066"));

        assertThat(dark.getOnBrandColor()).isEqualTo("#FFFFFF");
        assertThat(light.getOnBrandColor()).isEqualTo("#23252B");
    }
}
