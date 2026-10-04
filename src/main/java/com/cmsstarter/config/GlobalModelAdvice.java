package com.cmsstarter.config;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cmsstarter.domain.cart.CartService;
import com.cmsstarter.domain.content.DesignSettingsService;
import com.cmsstarter.domain.content.MenuService;
import com.cmsstarter.domain.content.NavLink;
import com.cmsstarter.domain.content.SiteBrand;

import lombok.RequiredArgsConstructor;

/** 모든 화면에서 쓰는 공통 모델(브랜드, 네비 메뉴, 장바구니 수). */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final DesignSettingsService design;
    private final MenuService menus;
    private final PortOneProperties portOne;
    private final CartService cart;

    @ModelAttribute("brand")
    SiteBrand brand() {
        return design.brand();
    }

    @ModelAttribute("navMenu")
    List<NavLink> navMenu() {
        return menus.navLinks();
    }

    @ModelAttribute("portone")
    PortOneProperties portOne() {
        return portOne;
    }

    @ModelAttribute("cartCount")
    int cartCount(Authentication auth) {
        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return 0;
        }
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return admin ? 0 : cart.count(auth.getName());
    }
}
