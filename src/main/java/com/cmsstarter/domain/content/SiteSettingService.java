package com.cmsstarter.domain.content;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * site_setting(key/value) 접근 + 메모리 스냅샷 캐시.
 * 저장/삭제가 커밋되면 캐시를 비우므로 변경이 즉시 반영된다. (단일 인스턴스 기준)
 */
@Service
@RequiredArgsConstructor
public class SiteSettingService {

    // 메인 레이아웃
    public static final String HERO_PRESET = "hero.preset";
    public static final String GRID_COLUMNS = "grid.columns";
    public static final Set<String> HERO_PRESETS = Set.of("BANNER", "FULL", "NONE", "SLIDE");
    public static final Set<String> GRID_OPTIONS = Set.of("2", "3", "4");
    /** 풀 이미지형: 오버레이 헤더 글자색(LIGHT=밝은 글자, DARK=어두운 글자) / 히어로 문구 표시 여부 */
    public static final String HERO_NAV_TONE = "hero.nav_tone";
    public static final String HERO_SHOW_TEXT = "hero.show_text";
    public static final Set<String> NAV_TONES = Set.of("LIGHT", "DARK");

    // 디자인(브랜드)
    public static final String FONT_PRESET = "font.preset";
    public static final String BRAND_NAME = "brand.name";
    public static final String BRAND_PRIMARY = "brand.primary";
    public static final String BRAND_BACKGROUND = "brand.background";
    public static final String BRAND_LOGO = "brand.logo";

    private final SiteSettingRepository repo;

    private volatile Map<String, String> cache;

    public Map<String, String> all() {
        Map<String, String> c = cache;
        if (c == null) {
            c = repo.findAll().stream().collect(Collectors.toUnmodifiableMap(SiteSetting::getKey, SiteSetting::getValue));
            cache = c;
        }
        return c;
    }

    public String get(String key, String defaultValue) {
        return all().getOrDefault(key, defaultValue);
    }

    @Transactional
    public void put(Map<String, String> values) {
        values.forEach((k, v) -> repo.save(new SiteSetting(k, v)));
        evictAfterCommit();
    }

    @Transactional
    public void remove(Collection<String> keys) {
        keys.forEach(k -> repo.findById(k).ifPresent(repo::delete));
        evictAfterCommit();
    }

    public String heroPreset() {
        String v = get(HERO_PRESET, "BANNER");
        return HERO_PRESETS.contains(v) ? v : "BANNER";
    }

    public int gridColumns() {
        String v = get(GRID_COLUMNS, "4");
        return GRID_OPTIONS.contains(v) ? Integer.parseInt(v) : 4;
    }

    public String heroNavTone() {
        String v = get(HERO_NAV_TONE, "LIGHT");
        return NAV_TONES.contains(v) ? v : "LIGHT";
    }

    public boolean heroShowText() {
        return !"false".equals(get(HERO_SHOW_TEXT, "true"));
    }

    @Transactional
    public void save(String heroPreset, String gridColumns) {
        save(heroPreset, gridColumns, null, null);
    }

    /** navTone / showText 가 null 이면 기존 값을 유지한다. 허용되지 않는 값이면 아무것도 저장하지 않는다. */
    @Transactional
    public void save(String heroPreset, String gridColumns, String navTone, String showText) {
        if (!HERO_PRESETS.contains(heroPreset) || !GRID_OPTIONS.contains(gridColumns)
                || (navTone != null && !NAV_TONES.contains(navTone))
                || (showText != null && !"true".equals(showText) && !"false".equals(showText))) {
            throw new IllegalArgumentException("허용되지 않는 설정 값입니다.");
        }
        Map<String, String> values = new java.util.HashMap<>();
        values.put(HERO_PRESET, heroPreset);
        values.put(GRID_COLUMNS, gridColumns);
        if (navTone != null) {
            values.put(HERO_NAV_TONE, navTone);
        }
        if (showText != null) {
            values.put(HERO_SHOW_TEXT, showText);
        }
        put(values);
    }

    private void evictAfterCommit() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    cache = null;
                }
            });
        } else {
            cache = null;
        }
    }
}
