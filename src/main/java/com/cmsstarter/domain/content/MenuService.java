package com.cmsstarter.domain.content;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import com.cmsstarter.domain.catalog.Category;
import com.cmsstarter.domain.catalog.CategoryRepository;
import com.cmsstarter.support.UrlSafety;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

    /** 표시할 메뉴가 하나도 없을 때의 기본 메뉴 */
    public static final NavLink DEFAULT_LINK = new NavLink("전체상품", "/products");

    private final MenuItemRepository menus;
    private final CategoryRepository categories;

    /** 스토어프론트 네비용: 표시 중이고 링크가 유효한 항목만, 없으면 기본 메뉴. */
    @Transactional(readOnly = true)
    public List<NavLink> navLinks() {
        List<NavLink> links = menus.findVisibleWithCategory().stream()
                .map(MenuService::toLink)
                .filter(Objects::nonNull)
                .toList();
        return links.isEmpty() ? List.of(DEFAULT_LINK) : links;
    }

    /** 메뉴 항목 → 링크. 대상이 없거나 안전하지 않으면 null. */
    static NavLink toLink(MenuItem m) {
        if (m.getTargetType() == MenuTargetType.CATEGORY) {
            Category c = m.getCategory();
            if (c == null || !c.isVisible()) {
                return null;
            }
            return new NavLink(m.getLabel(),
                    "/products?category=" + UriUtils.encodeQueryParam(c.getSlug(), StandardCharsets.UTF_8));
        }
        String url = UrlSafety.safeUrl(m.getUrl());
        return url == null ? null : new NavLink(m.getLabel(), url);
    }

    @Transactional(readOnly = true)
    public List<MenuItem> adminList() {
        return menus.findAllWithCategory();
    }

    /** 추가/수정. id 가 null 이면 맨 뒤에 추가한다. */
    @Transactional
    public MenuItem save(Long id, String label, MenuTargetType type, Long categoryId, String url, boolean visible) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("메뉴 이름을 입력하세요.");
        }
        if (label.trim().length() > 40) {
            throw new IllegalArgumentException("메뉴 이름은 40자 이하여야 합니다.");
        }
        MenuItem m = id == null ? new MenuItem() : menus.findById(id).orElseThrow();
        m.setLabel(label.trim());
        m.setTargetType(type);
        if (type == MenuTargetType.CATEGORY) {
            if (categoryId == null) {
                throw new IllegalArgumentException("연결할 카테고리를 선택하세요.");
            }
            m.setCategory(categories.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다.")));
            m.setUrl(null);
        } else {
            String safe = UrlSafety.safeUrl(url);
            if (safe == null) {
                throw new IllegalArgumentException("URL 은 / 로 시작하거나 http(s):// 형식이어야 합니다.");
            }
            m.setUrl(safe);
            m.setCategory(null);
        }
        m.setVisible(visible);
        if (id == null) {
            m.setSortOrder(menus.maxSortOrder() + 1);
        }
        return menus.save(m);
    }

    @Transactional
    public void delete(Long id) {
        menus.deleteById(id);
    }

    @Transactional
    public void reorder(List<Long> ids) {
        int order = 1;
        for (Long id : ids) {
            MenuItem m = menus.findById(id).orElse(null);
            if (m != null) {
                m.setSortOrder(order++);
            }
        }
    }

    @Transactional
    public void setVisible(Long id, boolean visible) {
        menus.findById(id).orElseThrow().setVisible(visible);
    }
}
