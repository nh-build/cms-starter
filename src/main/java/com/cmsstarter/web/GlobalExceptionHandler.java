package com.cmsstarter.web;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.cmsstarter.config.BrandProperties;

import lombok.RequiredArgsConstructor;

@ControllerAdvice(basePackages = "com.cmsstarter.web.shop")
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final BrandProperties brand;

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(IllegalArgumentException e, Model model) {
        // 예외 처리 경로에서는 @ModelAttribute 공통 모델이 적용되지 않으므로 직접 채운다.
        model.addAttribute("brand", brand);
        model.addAttribute("cartCount", 0);
        model.addAttribute("message", e.getMessage());
        return "shop/error";
    }
}
