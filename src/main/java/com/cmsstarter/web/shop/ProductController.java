package com.cmsstarter.web.shop;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmsstarter.domain.catalog.CategoryRepository;
import com.cmsstarter.domain.catalog.OptionType;
import com.cmsstarter.domain.catalog.Product;
import com.cmsstarter.domain.catalog.ProductService;
import com.cmsstarter.domain.catalog.ProductStatus;
import com.cmsstarter.domain.content.SiteSettingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService products;
    private final CategoryRepository categories;
    private final SiteSettingService settings;

    @GetMapping("/products")
    public String list(@RequestParam(required = false) String category,
                       @RequestParam(required = false) String sort,
                       @RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<Product> result = products.search(category, sort, q, page);
        model.addAttribute("page", result);
        model.addAttribute("categories", categories.findByVisibleTrueOrderBySortOrderAscIdAsc());
        model.addAttribute("currentCategory", category);
        model.addAttribute("sort", sort);
        model.addAttribute("q", q);
        model.addAttribute("gridColumns", settings.gridColumns());
        String title = switch (sort == null ? "" : sort) {
            case "new" -> "신상품";
            case "best" -> "베스트";
            default -> "전체상품";
        };
        model.addAttribute("pageTitle", title);
        model.addAttribute("baseUrl", org.springframework.web.util.UriComponentsBuilder.fromPath("/products")
                .queryParamIfPresent("category", java.util.Optional.ofNullable(category).filter(v -> !v.isBlank()))
                .queryParamIfPresent("sort", java.util.Optional.ofNullable(sort).filter(v -> !v.isBlank()))
                .queryParamIfPresent("q", java.util.Optional.ofNullable(q).filter(v -> !v.isBlank()))
                .build().encode().toUriString());
        return "shop/products";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product p = products.detail(id);
        if (p.getStatus() == ProductStatus.HIDDEN) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }
        model.addAttribute("product", p);
        model.addAttribute("colors", p.optionsOf(OptionType.COLOR));
        model.addAttribute("sizes", p.optionsOf(OptionType.SIZE));
        return "shop/product-detail";
    }
}
