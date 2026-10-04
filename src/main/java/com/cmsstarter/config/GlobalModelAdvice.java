package com.cmsstarter.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cmsstarter.domain.cart.CartService;

import lombok.RequiredArgsConstructor;

/** 모든 화면에서 쓰는 공통 모델(브랜드, 장바구니 수). */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final BrandProperties brand;
    private final PortOneProperties portOne;
    private final CartService cart;

    @ModelAttribute("brand")
    BrandProperties brand() {
        return brand;
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
