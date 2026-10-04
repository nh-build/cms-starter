package com.cmsstarter.web.shop;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cmsstarter.domain.catalog.CategoryRepository;
import com.cmsstarter.domain.catalog.ProductService;
import com.cmsstarter.domain.content.Banner;
import com.cmsstarter.domain.content.BannerRepository;
import com.cmsstarter.domain.content.HeroLayout;
import com.cmsstarter.domain.content.HomeSection;
import com.cmsstarter.domain.content.HomeSectionRepository;
import com.cmsstarter.domain.content.SectionType;
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
        List<HomeSection> visibleSections = sections.findByVisibleTrueOrderBySortOrderAscIdAsc();
        List<Banner> visibleBanners = banners.findByVisibleTrueOrderBySortOrderAscIdAsc();
        String heroPreset = settings.heroPreset();
        SectionType first = visibleSections.isEmpty() ? null : visibleSections.get(0).getType();
        model.addAttribute("sections", visibleSections);
        model.addAttribute("banners", visibleBanners);
        // 풀 이미지형: 헤더를 히어로 이미지 위에 겹친다 (조건은 HeroLayout 참고)
        model.addAttribute("heroOverlay", HeroLayout.overlayHeader(heroPreset, first, !visibleBanners.isEmpty()));
        model.addAttribute("heroNavTone", settings.heroNavTone().toLowerCase());
        model.addAttribute("heroShowText", settings.heroShowText());
        model.addAttribute("categories", categories.findByVisibleTrueOrderBySortOrderAscIdAsc());
        model.addAttribute("featured", products.featured(8));
        model.addAttribute("newest", products.newest(8));
        model.addAttribute("best", products.best(8));
        model.addAttribute("heroPreset", heroPreset);
        model.addAttribute("gridColumns", settings.gridColumns());
        return "shop/home";
    }
}
