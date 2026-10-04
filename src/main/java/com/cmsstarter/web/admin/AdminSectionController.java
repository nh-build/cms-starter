package com.cmsstarter.web.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmsstarter.domain.content.HomeSection;
import com.cmsstarter.domain.content.HomeSectionRepository;
import com.cmsstarter.domain.content.SiteSettingService;

import lombok.RequiredArgsConstructor;

/** 메인 섹션 블록 관리: 드래그 정렬(SortableJS) + 표시/숨김 + 히어로 프리셋 + 그리드 열 수. */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/sections")
public class AdminSectionController {

    private final HomeSectionRepository sections;
    private final SiteSettingService settings;

    @GetMapping
    public String page(Model model) {
        model.addAttribute("active", "sections");
        model.addAttribute("sections", sections.findAllByOrderBySortOrderAscIdAsc());
        model.addAttribute("heroPreset", settings.heroPreset());
        model.addAttribute("gridColumns", settings.gridColumns());
        return "admin/sections";
    }

    /** body: { "ids": [3,1,2] } — 배열 순서가 곧 새 정렬 순서. */
    @PostMapping("/order")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> reorder(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        if (ids == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "ids 가 필요합니다."));
        }
        int order = 1;
        for (Long id : ids) {
            HomeSection s = sections.findById(id).orElse(null);
            if (s != null) {
                s.setSortOrder(order++);
            }
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/{id}/visible")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> toggle(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        HomeSection s = sections.findById(id).orElseThrow();
        s.setVisible(Boolean.TRUE.equals(body.get("visible")));
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/{id}/title")
    public String rename(@PathVariable Long id, @RequestParam String title, RedirectAttributes ra) {
        if (!title.isBlank()) {
            HomeSection s = sections.findById(id).orElseThrow();
            s.setTitle(title.trim());
            sections.save(s);
        }
        ra.addFlashAttribute("message", "저장했습니다.");
        return "redirect:/admin/sections";
    }

    @PostMapping("/settings")
    public String saveSettings(@RequestParam String heroPreset, @RequestParam String gridColumns, RedirectAttributes ra) {
        try {
            settings.save(heroPreset, gridColumns);
            ra.addFlashAttribute("message", "저장했습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sections";
    }
}
