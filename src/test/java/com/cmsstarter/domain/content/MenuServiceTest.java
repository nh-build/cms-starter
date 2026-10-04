package com.cmsstarter.domain.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.cmsstarter.domain.catalog.Category;
import com.cmsstarter.domain.catalog.CategoryRepository;

class MenuServiceTest {

    private final MenuItemRepository menus = mock(MenuItemRepository.class);
    private final CategoryRepository categories = mock(CategoryRepository.class);
    private final MenuService service = new MenuService(menus, categories);

    private static MenuItem url(String label, String url) {
        MenuItem m = new MenuItem();
        m.setLabel(label);
        m.setTargetType(MenuTargetType.URL);
        m.setUrl(url);
        return m;
    }

    private static Category category(String slug, boolean visible) {
        Category c = new Category();
        c.setId(7L);
        c.setName("아우터");
        c.setSlug(slug);
        c.setVisible(visible);
        return c;
    }

    private static MenuItem categoryItem(String label, Category c) {
        MenuItem m = new MenuItem();
        m.setLabel(label);
        m.setTargetType(MenuTargetType.CATEGORY);
        m.setCategory(c);
        return m;
    }

    @Test
    void categoryMenuLinksToProductListFilteredBySlug() {
        NavLink link = MenuService.toLink(categoryItem("아우터", category("outer", true)));

        assertThat(link.getLabel()).isEqualTo("아우터");
        assertThat(link.getHref()).isEqualTo("/products?category=outer");
    }

    @Test
    void nonAsciiSlugIsPercentEncoded() {
        NavLink link = MenuService.toLink(categoryItem("니트", category("니트", true)));

        assertThat(link.getHref()).isEqualTo("/products?category=%EB%8B%88%ED%8A%B8");
    }

    @Test
    void hiddenOrMissingCategoryAndUnsafeUrlProduceNoLink() {
        assertThat(MenuService.toLink(categoryItem("x", category("outer", false)))).isNull();
        assertThat(MenuService.toLink(categoryItem("x", null))).isNull();
        assertThat(MenuService.toLink(url("x", "javascript:alert(1)"))).isNull();
    }

    @Test
    void urlMenuKeepsSafeUrl() {
        assertThat(MenuService.toLink(url("이벤트", "/#event")).getHref()).isEqualTo("/#event");
    }

    @Test
    void fallsBackToDefaultMenuWhenNothingIsVisible() {
        when(menus.findVisibleWithCategory()).thenReturn(List.of());

        assertThat(service.navLinks()).containsExactly(MenuService.DEFAULT_LINK);
    }

    @Test
    void fallsBackToDefaultMenuWhenEveryItemIsInvalid() {
        when(menus.findVisibleWithCategory()).thenReturn(List.of(url("bad", "javascript:x")));

        assertThat(service.navLinks()).containsExactly(MenuService.DEFAULT_LINK);
    }

    @Test
    void skipsInvalidItemsButKeepsTheRestInOrder() {
        when(menus.findVisibleWithCategory()).thenReturn(List.of(
                url("전체상품", "/products"), url("bad", "javascript:x"), url("베스트", "/products?sort=best")));

        assertThat(service.navLinks()).extracting(NavLink::getLabel).containsExactly("전체상품", "베스트");
    }

    @Test
    void saveRejectsBlankLabelMissingCategoryAndUnsafeUrl() {
        assertThatThrownBy(() -> service.save(null, " ", MenuTargetType.URL, null, "/x", true))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.save(null, "a", MenuTargetType.CATEGORY, null, null, true))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.save(null, "a", MenuTargetType.URL, null, "javascript:x", true))
                .isInstanceOf(IllegalArgumentException.class);
        verify(menus, never()).save(any());
    }

    @Test
    void newItemIsAppendedAtTheEnd() {
        when(menus.maxSortOrder()).thenReturn(4);
        when(menus.save(any(MenuItem.class))).thenAnswer(i -> i.getArgument(0));

        MenuItem saved = service.save(null, "신규", MenuTargetType.URL, null, "/sale", true);

        assertThat(saved.getSortOrder()).isEqualTo(5);
        assertThat(saved.getUrl()).isEqualTo("/sale");
        assertThat(saved.getCategory()).isNull();
    }

    @Test
    void categoryTargetClearsUrlAndLinksCategory() {
        Category c = category("outer", true);
        when(categories.findById(7L)).thenReturn(Optional.of(c));
        when(menus.maxSortOrder()).thenReturn(0);
        when(menus.save(any(MenuItem.class))).thenAnswer(i -> i.getArgument(0));

        MenuItem saved = service.save(null, "아우터", MenuTargetType.CATEGORY, 7L, "/ignored", true);

        assertThat(saved.getCategory()).isSameAs(c);
        assertThat(saved.getUrl()).isNull();
    }
}
