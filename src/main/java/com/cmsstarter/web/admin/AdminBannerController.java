package com.cmsstarter.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmsstarter.domain.content.Banner;
import com.cmsstarter.domain.content.BannerRepository;
import com.cmsstarter.support.FileStorage;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/banners")
public class AdminBannerController {

    private final BannerRepository banners;
    private final FileStorage storage;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "banners");
        model.addAttribute("banners", banners.findAllByOrderBySortOrderAscIdAsc());
        return "admin/banners";
    }

    @PostMapping
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String title,
                       @RequestParam(required = false) String eyebrow,
                       @RequestParam(required = false) String subtitle,
                       @RequestParam(required = false) String ctaLabel,
                       @RequestParam(required = false) String ctaUrl,
                       @RequestParam(defaultValue = "0") int sortOrder,
                       @RequestParam(defaultValue = "false") boolean visible,
                       @RequestParam(required = false) MultipartFile imageFile,
                       RedirectAttributes ra) {
        if (title.isBlank()) {
            ra.addFlashAttribute("error", "배너 제목을 입력하세요.");
            return "redirect:/admin/banners";
        }
        Banner b = id == null ? new Banner() : banners.findById(id).orElseThrow();
        try {
            String path = storage.store(imageFile);
            if (path != null) {
                storage.delete(b.getImage());
                b.setImage(path);
            }
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/banners";
        }
        b.setTitle(title.trim());
        b.setEyebrow(eyebrow);
        b.setSubtitle(subtitle);
        b.setCtaLabel(ctaLabel);
        b.setCtaUrl(safeUrl(ctaUrl));
        b.setSortOrder(sortOrder);
        b.setVisible(visible);
        banners.save(b);
        ra.addFlashAttribute("message", "저장했습니다.");
        return "redirect:/admin/banners";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        banners.findById(id).ifPresent(b -> {
            storage.delete(b.getImage());
            banners.delete(b);
        });
        ra.addFlashAttribute("message", "삭제했습니다.");
        return "redirect:/admin/banners";
    }

    /** javascript: 등 위험한 스킴을 막고 사이트 내부 경로 또는 http(s) 만 허용. */
    private static String safeUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        String u = url.trim();
        return u.startsWith("/") && !u.startsWith("//") || u.startsWith("http://") || u.startsWith("https://") ? u : null;
    }
}
