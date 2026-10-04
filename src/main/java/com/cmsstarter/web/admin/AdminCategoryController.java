package com.cmsstarter.web.admin;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmsstarter.domain.catalog.Category;
import com.cmsstarter.domain.catalog.CategoryRepository;
import com.cmsstarter.domain.catalog.ProductRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryRepository categories;
    private final ProductRepository products;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "categories");
        model.addAttribute("categories", categories.findAllByOrderBySortOrderAscIdAsc());
        return "admin/categories";
    }

    @PostMapping
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String name,
                       @RequestParam(required = false) String slug,
                       @RequestParam(defaultValue = "0") int sortOrder,
                       @RequestParam(defaultValue = "false") boolean visible,
                       RedirectAttributes ra) {
        if (name.isBlank()) {
            ra.addFlashAttribute("error", "카테고리 이름을 입력하세요.");
            return "redirect:/admin/categories";
        }
        Category c = id == null ? new Category() : categories.findById(id).orElseThrow();
        String s = (slug == null || slug.isBlank() ? name : slug).trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}0-9]+", "-").replaceAll("^-|-$", "");
        if (s.isEmpty()) {
            s = "cat-" + System.currentTimeMillis();
        }
        boolean taken = categories.findBySlug(s).map(o -> !o.getId().equals(c.getId())).orElse(false);
        if (taken) {
            ra.addFlashAttribute("error", "이미 사용 중인 슬러그입니다: " + s);
            return "redirect:/admin/categories";
        }
        c.setName(name.trim());
        c.setSlug(s);
        c.setSortOrder(sortOrder);
        c.setVisible(visible);
        categories.save(c);
        ra.addFlashAttribute("message", "저장했습니다.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        if (products.countByCategoryId(id) > 0) {
            ra.addFlashAttribute("error", "상품이 연결된 카테고리는 삭제할 수 없습니다. 먼저 상품의 카테고리를 변경하세요.");
        } else {
            categories.deleteById(id);
            ra.addFlashAttribute("message", "삭제했습니다.");
        }
        return "redirect:/admin/categories";
    }
}
