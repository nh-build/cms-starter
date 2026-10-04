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

    /** 화면에 보여줄 순서와 이름 (저장 값은 SiteSettingService.HERO_PRESETS) */
    private static final List<PresetChoice> HERO_PRESET_CHOICES = List.of(
            new PresetChoice("BANNER", "가로 배너형"), new PresetChoice("FULL", "풀 이미지형"),
            new PresetChoice("NONE", "배너 없음"), new PresetChoice("SLIDE", "슬라이드형"));

    public record PresetChoice(String key, String label) {
        public String getKey() {
            return key;
        }

        public String getLabel() {
            return label;
        }
    }

    private final HomeSectionRepository sections;
    private final SiteSettingService settings;

    @GetMapping
    public String page(Model model) {
        model.addAttribute("active", "sections");
        model.addAttribute("sections", sections.findAllByOrderBySortOrderAscIdAsc());
        model.addAttribute("heroPresets", HERO_PRESET_CHOICES);
        model.addAttribute("heroPreset", settings.heroPreset());
        model.addAttribute("gridColumns", settings.gridColumns());
        model.addAttribute("heroNavTone", settings.heroNavTone());
        model.addAttribute("heroShowText", settings.heroShowText());
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

    /** 썸네일/아이콘 클릭 즉시 저장(fetch). */
    @PostMapping(value = "/settings", headers = "X-Requested-With=fetch")
    @ResponseBody
    public ResponseEntity<?> saveSettingsAjax(@RequestParam String heroPreset, @RequestParam String gridColumns,
                                              @RequestParam(required = false) String navTone,
                                              @RequestParam(required = false) String showText) {
        try {
            settings.save(heroPreset, gridColumns, navTone, showText);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** JS 가 꺼진 환경을 위한 일반 form 저장. */
    @PostMapping("/settings")
    public String saveSettings(@RequestParam String heroPreset, @RequestParam String gridColumns,
                               @RequestParam(required = false) String navTone,
                               @RequestParam(required = false) String showText, RedirectAttributes ra) {
        try {
            settings.save(heroPreset, gridColumns, navTone, showText);
            ra.addFlashAttribute("message", "저장했습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sections";
    }
}
