package com.cmsstarter.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmsstarter.domain.content.DesignForm;
import com.cmsstarter.domain.content.DesignSettingsService;
import com.cmsstarter.domain.content.FontPreset;
import com.cmsstarter.domain.content.SiteBrand;

import lombok.RequiredArgsConstructor;

/** 디자인(브랜드) 설정: 사이트명, 로고, 포인트색/배경색, 폰트. 저장하면 스토어와 관리자에 즉시 반영된다. */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/design")
public class AdminDesignController {

    private final DesignSettingsService design;

    @GetMapping
    public String page(Model model) {
        SiteBrand b = design.brand();
        DesignForm form = new DesignForm();
        form.setBrandName(b.getName());
        form.setPrimaryColor(b.getPrimaryColor());
        form.setBackgroundColor(b.getBackgroundColor());
        form.setFontPreset(b.getFontPreset().name());
        model.addAttribute("active", "design");
        model.addAttribute("form", form);
        model.addAttribute("fonts", FontPreset.values());
        return "admin/design";
    }

    @PostMapping
    public String save(@ModelAttribute DesignForm form, RedirectAttributes ra) {
        try {
            design.save(form);
            ra.addFlashAttribute("message", "저장했습니다. 스토어와 관리자에 바로 반영됩니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/design";
    }

    @PostMapping("/reset")
    public String reset(RedirectAttributes ra) {
        design.reset();
        ra.addFlashAttribute("message", "기본값(application.yml)으로 되돌렸습니다.");
        return "redirect:/admin/design";
    }
}
