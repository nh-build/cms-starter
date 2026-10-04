package com.cmsstarter.domain.content;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteSettingService {

    public static final String HERO_PRESET = "hero.preset";
    public static final String GRID_COLUMNS = "grid.columns";
    public static final Set<String> HERO_PRESETS = Set.of("BANNER", "NONE", "SLIDE");
    public static final Set<String> GRID_OPTIONS = Set.of("2", "3", "4");

    private final SiteSettingRepository repo;

    @Transactional(readOnly = true)
    public String heroPreset() {
        return repo.findById(HERO_PRESET).map(SiteSetting::getValue).orElse("BANNER");
    }

    @Transactional(readOnly = true)
    public int gridColumns() {
        return Integer.parseInt(repo.findById(GRID_COLUMNS).map(SiteSetting::getValue).orElse("4"));
    }

    @Transactional
    public void save(String heroPreset, String gridColumns) {
        if (!HERO_PRESETS.contains(heroPreset) || !GRID_OPTIONS.contains(gridColumns)) {
            throw new IllegalArgumentException("허용되지 않는 설정 값입니다.");
        }
        repo.save(new SiteSetting(HERO_PRESET, heroPreset));
        repo.save(new SiteSetting(GRID_COLUMNS, gridColumns));
    }
}
