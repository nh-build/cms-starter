package com.cmsstarter.domain.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import com.cmsstarter.config.BrandProperties;
import com.cmsstarter.support.FileStorage;

class DesignSettingsServiceTest {

    private final SiteSettingService settings = mock(SiteSettingService.class);
    private final FileStorage storage = mock(FileStorage.class);
    private final DesignSettingsService service = new DesignSettingsService(new BrandProperties(), settings, storage);

    private static DesignForm validForm() {
        DesignForm f = new DesignForm();
        f.setBrandName("  MOOD  ");
        f.setPrimaryColor("#abc");
        f.setBackgroundColor("#ffffff");
        f.setFontPreset("NOTO_SANS_KR");
        return f;
    }

    @Test
    @SuppressWarnings("unchecked")
    void savesNormalizedValues() {
        service.save(validForm());

        ArgumentCaptor<Map<String, String>> saved = ArgumentCaptor.forClass(Map.class);
        verify(settings).put(saved.capture());
        assertThat(saved.getValue())
                .containsEntry(SiteSettingService.BRAND_NAME, "MOOD")
                .containsEntry(SiteSettingService.BRAND_PRIMARY, "#AABBCC")
                .containsEntry(SiteSettingService.BRAND_BACKGROUND, "#FFFFFF")
                .containsEntry(SiteSettingService.FONT_PRESET, "NOTO_SANS_KR")
                .doesNotContainKey(SiteSettingService.BRAND_LOGO);
    }

    @Test
    void rejectsInvalidInputAndSavesNothing() {
        DesignForm blankName = validForm();
        blankName.setBrandName("   ");
        DesignForm longName = validForm();
        longName.setBrandName("x".repeat(41));
        DesignForm badPrimary = validForm();
        badPrimary.setPrimaryColor("red;}</style>");
        DesignForm badBackground = validForm();
        badBackground.setBackgroundColor("not-a-color");
        DesignForm darkBackground = validForm();
        darkBackground.setBackgroundColor("#111111");
        DesignForm badFont = validForm();
        badFont.setFontPreset("COMIC_SANS");
        DesignForm nullFont = validForm();
        nullFont.setFontPreset(null);

        for (DesignForm f : List.of(blankName, longName, badPrimary, badBackground, darkBackground, badFont, nullFont)) {
            assertThatThrownBy(() -> service.save(f)).isInstanceOf(IllegalArgumentException.class);
        }
        verify(settings, never()).put(anyMap());
        verify(storage, never()).store(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void replacingLogoStoresNewFileAndDeletesTheOldOne() {
        when(settings.get(SiteSettingService.BRAND_LOGO, null)).thenReturn("/uploads/old.png");
        when(storage.store(any())).thenReturn("/uploads/new.png");
        DesignForm f = validForm();
        f.setLogoFile(new MockMultipartFile("logoFile", "logo.png", "image/png", new byte[] { 1 }));

        service.save(f);

        ArgumentCaptor<Map<String, String>> saved = ArgumentCaptor.forClass(Map.class);
        verify(settings).put(saved.capture());
        assertThat(saved.getValue()).containsEntry(SiteSettingService.BRAND_LOGO, "/uploads/new.png");
        verify(storage).delete("/uploads/old.png");
    }

    @Test
    void removingLogoClearsSettingAndDeletesFile() {
        when(settings.get(SiteSettingService.BRAND_LOGO, null)).thenReturn("/uploads/old.png");
        DesignForm f = validForm();
        f.setRemoveLogo(true);

        service.save(f);

        verify(settings).remove(List.of(SiteSettingService.BRAND_LOGO));
        verify(storage).delete("/uploads/old.png");
    }

    @Test
    void unchangedLogoIsKeptWhenNoFileAndNoRemoval() {
        when(settings.get(SiteSettingService.BRAND_LOGO, null)).thenReturn("/uploads/old.png");

        service.save(validForm());

        verify(settings, never()).remove(any());
        verify(storage, never()).delete(anyString());
    }

    @Test
    void resetClearsEveryBrandKeyAndTheUploadedLogo() {
        when(settings.get(SiteSettingService.BRAND_LOGO, null)).thenReturn("/uploads/old.png");

        service.reset();

        verify(settings).remove(List.of(SiteSettingService.BRAND_NAME, SiteSettingService.BRAND_PRIMARY,
                SiteSettingService.BRAND_BACKGROUND, SiteSettingService.BRAND_LOGO, SiteSettingService.FONT_PRESET));
        verify(storage).delete("/uploads/old.png");
    }
}
