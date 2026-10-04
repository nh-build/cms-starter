package com.cmsstarter.domain.content;

import java.util.Map;

import com.cmsstarter.config.BrandProperties;
import com.cmsstarter.support.ColorUtil;

import lombok.Getter;

/**
 * 화면에 실제로 적용되는 브랜드 값. application.yml(brand.*) 이 기본값이고, 관리자(DB) 설정이 있으면 그 값이 우선한다.
 * 템플릿에는 모델 이름 {@code brand} 로 전달된다.
 */
@Getter
public class SiteBrand {

    private static final String DEFAULT_PRIMARY = "#4E90C4";
    private static final String DEFAULT_BACKGROUND = "#F7FBFE";

    private final String name;
    private final String logoPath;
    private final String primaryColor;
    private final String backgroundColor;
    private final String onBrandColor;
    private final FontPreset fontPreset;
    private final String tagline;
    private final String company;
    private final String contactEmail;

    private SiteBrand(String name, String logoPath, String primary, String background, FontPreset font,
                      String tagline, String company, String contactEmail) {
        this.name = name;
        this.logoPath = logoPath;
        this.primaryColor = primary;
        this.backgroundColor = background;
        this.onBrandColor = ColorUtil.onColor(primary);
        this.fontPreset = font;
        this.tagline = tagline;
        this.company = company;
        this.contactEmail = contactEmail;
    }

    /** 기본값(yml)에 DB 설정을 덮어써서 최종 브랜드를 만든다. 유효하지 않은 값은 무시한다. */
    public static SiteBrand resolve(BrandProperties defaults, Map<String, String> overrides) {
        String name = nonBlank(overrides.get(SiteSettingService.BRAND_NAME), defaults.getName());
        String logo = nonBlank(overrides.get(SiteSettingService.BRAND_LOGO), defaults.getLogoPath());
        String primary = color(overrides.get(SiteSettingService.BRAND_PRIMARY), color(defaults.getPrimaryColor(), DEFAULT_PRIMARY));
        String background = color(overrides.get(SiteSettingService.BRAND_BACKGROUND),
                color(defaults.getBackgroundColor(), DEFAULT_BACKGROUND));
        FontPreset font = FontPreset.fromKey(
                nonBlank(overrides.get(SiteSettingService.FONT_PRESET), defaults.getFontPreset()));
        return new SiteBrand(name, logo, primary, background, font,
                defaults.getTagline(), defaults.getCompany(), defaults.getContactEmail());
    }

    public boolean hasLogo() {
        return logoPath != null && !logoPath.isBlank();
    }

    public String getFontCssUrl() {
        return fontPreset.getCssUrl();
    }

    /** <style> 안에 그대로 출력된다. 색상은 검증된 HEX, 폰트는 enum 값만 사용하므로 주입 위험이 없다. */
    public String getCssVars() {
        return ":root{--brand:" + primaryColor + ";--bg:" + backgroundColor + ";--on-brand:" + onBrandColor
                + ";--font-family:" + fontPreset.getFamilyStack() + ";}";
    }

    private static String nonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String color(String value, String fallback) {
        String n = ColorUtil.normalize(value);
        return n != null ? n : fallback;
    }
}
