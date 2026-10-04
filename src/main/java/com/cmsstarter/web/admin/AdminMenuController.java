package com.cmsstarter.web.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmsstarter.domain.catalog.CategoryRepository;
import com.cmsstarter.domain.content.MenuService;
import com.cmsstarter.domain.content.MenuTargetType;

import lombok.RequiredArgsConstructor;

/** 스토어프론트 상단 네비 메뉴 관리: 추가/수정/삭제, 드래그 순서, 표시 토글. */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/menus")
public class AdminMenuController {

    private final MenuService menus;
    private final CategoryRepository categories;

    @GetMapping
    public String page(Model model) {
        model.addAttribute("active", "menus");
        model.addAttribute("items", menus.adminList());
        model.addAttribute("categories", categories.findAllByOrderBySortOrderAscIdAsc());
        return "admin/menus";
    }

    @PostMapping
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String label,
                       @RequestParam MenuTargetType targetType,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String url,
                       @RequestParam(defaultValue = "false") boolean visible,
                       RedirectAttributes ra) {
        try {
            menus.save(id, label, targetType, categoryId, url, visible);
            ra.addFlashAttribute("message", "저장했습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/menus";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        menus.delete(id);
        ra.addFlashAttribute("message", "삭제했습니다.");
        return "redirect:/admin/menus";
    }

    /** body: { "ids": [3,1,2] } — 배열 순서가 새 정렬 순서. */
    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity<?> reorder(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        if (ids == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "ids 가 필요합니다."));
        }
        menus.reorder(ids);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/{id}/visible")
    @ResponseBody
    public ResponseEntity<?> visible(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        menus.setVisible(id, Boolean.TRUE.equals(body.get("visible")));
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
