package com.cmsstarter.web.shop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cmsstarter.domain.catalog.CategoryRepository;
import com.cmsstarter.domain.catalog.ProductService;
import com.cmsstarter.domain.content.BannerRepository;
import com.cmsstarter.domain.content.HomeSectionRepository;
import com.cmsstarter.domain.content.SiteSettingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomeSectionRepository sections;
    private final BannerRepository banners;
    private final CategoryRepository categories;
    private final ProductService products;
    private final SiteSettingService settings;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("sections", sections.findByVisibleTrueOrderBySortOrderAscIdAsc());
        model.addAttribute("banners", banners.findByVisibleTrueOrderBySortOrderAscIdAsc());
        model.addAttribute("categories", categories.findByVisibleTrueOrderBySortOrderAscIdAsc());
        model.addAttribute("featured", products.featured(8));
        model.addAttribute("newest", products.newest(8));
        model.addAttribute("best", products.best(8));
        model.addAttribute("heroPreset", settings.heroPreset());
        model.addAttribute("gridColumns", settings.gridColumns());
        return "shop/home";
    }
}
