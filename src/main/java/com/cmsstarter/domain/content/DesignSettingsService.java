package com.cmsstarter.domain.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmsstarter.config.BrandProperties;
import com.cmsstarter.support.ColorUtil;
import com.cmsstarter.support.FileStorage;

import lombok.RequiredArgsConstructor;

/** 디자인(브랜드) 설정의 단일 진입점: 기본값(yml) + 관리자 설정(DB) 을 합쳐 현재 브랜드를 제공하고, 저장/초기화를 처리한다. */
@Service
@RequiredArgsConstructor
public class DesignSettingsService {

    private static final List<String> BRAND_KEYS = List.of(
            SiteSettingService.BRAND_NAME, SiteSettingService.BRAND_PRIMARY, SiteSettingService.BRAND_BACKGROUND,
            SiteSettingService.BRAND_LOGO, SiteSettingService.FONT_PRESET);

    private final BrandProperties defaults;
    private final SiteSettingService settings;
    private final FileStorage storage;

    public SiteBrand brand() {
        return SiteBrand.resolve(defaults, settings.all());
    }

    /** 모든 값을 검증한 뒤에만 저장한다. 하나라도 잘못되면 아무것도 바뀌지 않는다. */
    @Transactional
    public void save(DesignForm form) {
        String name = form.getBrandName() == null ? "" : form.getBrandName().trim();
        if (name.isEmpty() || name.length() > 40) {
            throw new IllegalArgumentException("사이트명은 1~40자로 입력하세요.");
        }
        String primary = ColorUtil.normalize(form.getPrimaryColor());
        if (primary == null) {
            throw new IllegalArgumentException("포인트색은 #4E90C4 형식의 색상 코드여야 합니다.");
        }
        String background = ColorUtil.normalize(form.getBackgroundColor());
        if (background == null) {
            throw new IllegalArgumentException("배경색은 #F7FBFE 형식의 색상 코드여야 합니다.");
        }
        if (!ColorUtil.isLightEnoughForBackground(background)) {
            throw new IllegalArgumentException("배경색이 너무 어두워 본문 글자가 읽히지 않습니다. 더 밝은 색을 선택하세요.");
        }
        FontPreset font = FontPreset.fromKey(form.getFontPreset());
        if (form.getFontPreset() == null || !font.name().equalsIgnoreCase(form.getFontPreset().trim())) {
            throw new IllegalArgumentException("지원하지 않는 폰트입니다.");
        }

        String previousLogo = settings.get(SiteSettingService.BRAND_LOGO, null);
        String newLogo = storage.store(form.getLogoFile());   // 이미지가 아니면 IllegalArgumentException

        Map<String, String> values = new HashMap<>();
        values.put(SiteSettingService.BRAND_NAME, name);
        values.put(SiteSettingService.BRAND_PRIMARY, primary);
        values.put(SiteSettingService.BRAND_BACKGROUND, background);
        values.put(SiteSettingService.FONT_PRESET, font.name());
        if (newLogo != null) {
            values.put(SiteSettingService.BRAND_LOGO, newLogo);
        }
        settings.put(values);

        if (newLogo != null) {
            storage.delete(previousLogo);
        } else if (form.isRemoveLogo()) {
            settings.remove(List.of(SiteSettingService.BRAND_LOGO));
            storage.delete(previousLogo);
        }
    }

    /** 관리자가 저장한 브랜드 값을 모두 지우고 application.yml 기본값으로 되돌린다. */
    @Transactional
    public void reset() {
        String previousLogo = settings.get(SiteSettingService.BRAND_LOGO, null);
        settings.remove(BRAND_KEYS);
        storage.delete(previousLogo);
    }
}
