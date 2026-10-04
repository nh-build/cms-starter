package com.cmsstarter.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmsstarter.domain.catalog.CategoryRepository;
import com.cmsstarter.domain.catalog.Product;
import com.cmsstarter.domain.catalog.ProductAdminService;
import com.cmsstarter.domain.catalog.ProductForm;
import com.cmsstarter.domain.catalog.ProductService;
import com.cmsstarter.domain.catalog.ProductStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@org.springframework.web.bind.annotation.RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService products;
    private final ProductAdminService admin;
    private final CategoryRepository categories;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("active", "products");
        model.addAttribute("page", products.adminList(page));
        return "admin/products";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        prepare(model, null);
        model.addAttribute("form", new ProductForm());
        return "admin/product-form";
    }

    @GetMapping("/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Product p = products.detail(id);
        prepare(model, p);
        model.addAttribute("form", admin.toForm(p));
        return "admin/product-form";
    }

    @PostMapping({ "", "/{id}" })
    public String save(@PathVariable(required = false) Long id,
                       @Valid @ModelAttribute("form") ProductForm form,
                       BindingResult binding, Model model, RedirectAttributes ra) {
        if (binding.hasErrors()) {
            prepare(model, id == null ? null : products.detail(id));
            return "admin/product-form";
        }
        try {
            admin.save(id, form);
        } catch (IllegalArgumentException e) {
            binding.reject("upload", e.getMessage());
            prepare(model, id == null ? null : products.detail(id));
            return "admin/product-form";
        }
        ra.addFlashAttribute("message", "저장했습니다.");
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        admin.delete(id);
        ra.addFlashAttribute("message", "삭제했습니다.");
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/images/{imageId}/delete")
    public String deleteImage(@PathVariable Long id, @PathVariable Long imageId) {
        admin.deleteImage(id, imageId);
        return "redirect:/admin/products/" + id;
    }

    private void prepare(Model model, Product product) {
        model.addAttribute("active", "products");
        model.addAttribute("product", product);
        model.addAttribute("categories", categories.findAllByOrderBySortOrderAscIdAsc());
        model.addAttribute("statuses", ProductStatus.values());
    }
}
