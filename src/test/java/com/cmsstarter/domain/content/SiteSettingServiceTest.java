package com.cmsstarter.domain.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SiteSettingServiceTest {

    private final SiteSettingRepository repo = mock(SiteSettingRepository.class);
    private final SiteSettingService service = new SiteSettingService(repo);

    @Test
    void defaultsWhenNothingStored() {
        when(repo.findAll()).thenReturn(List.of());

        assertThat(service.heroPreset()).isEqualTo("BANNER");
        assertThat(service.gridColumns()).isEqualTo(4);
        assertThat(service.heroNavTone()).isEqualTo("LIGHT");
        assertThat(service.heroShowText()).isTrue();
    }

    @Test
    void readsStoredValuesAndIgnoresGarbage() {
        when(repo.findAll()).thenReturn(List.of(
                new SiteSetting(SiteSettingService.HERO_PRESET, "FULL"),
                new SiteSetting(SiteSettingService.GRID_COLUMNS, "99"),
                new SiteSetting(SiteSettingService.HERO_NAV_TONE, "DARK"),
                new SiteSetting(SiteSettingService.HERO_SHOW_TEXT, "false")));

        assertThat(service.heroPreset()).isEqualTo("FULL");
        assertThat(service.gridColumns()).isEqualTo(4);
        assertThat(service.heroNavTone()).isEqualTo("DARK");
        assertThat(service.heroShowText()).isFalse();
    }

    @Test
    void savesAllLayoutSettings() {
        service.save("FULL", "3", "DARK", "false");

        ArgumentCaptor<SiteSetting> saved = ArgumentCaptor.forClass(SiteSetting.class);
        verify(repo, times(4)).save(saved.capture());
        assertThat(saved.getAllValues()).extracting(SiteSetting::getKey, SiteSetting::getValue).containsExactlyInAnyOrder(
                org.assertj.core.groups.Tuple.tuple("hero.preset", "FULL"),
                org.assertj.core.groups.Tuple.tuple("grid.columns", "3"),
                org.assertj.core.groups.Tuple.tuple("hero.nav_tone", "DARK"),
                org.assertj.core.groups.Tuple.tuple("hero.show_text", "false"));
    }

    @Test
    void optionalFullSettingsAreLeftUntouchedWhenOmitted() {
        service.save("BANNER", "4");

        verify(repo, times(2)).save(any(SiteSetting.class));
    }

    @Test
    void invalidValuesAreRejectedAndNothingIsSaved() {
        assertThatThrownBy(() -> service.save("EVIL", "4", null, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.save("FULL", "9", null, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.save("FULL", "4", "NEON", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.save("FULL", "4", "LIGHT", "maybe")).isInstanceOf(IllegalArgumentException.class);
        verify(repo, never()).save(any());
    }
}
