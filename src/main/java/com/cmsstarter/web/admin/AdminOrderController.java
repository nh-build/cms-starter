package com.cmsstarter.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmsstarter.domain.order.OrderService;
import com.cmsstarter.domain.order.OrderStatus;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orders;

    @GetMapping
    public String list(@RequestParam(required = false) OrderStatus status,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("active", "orders");
        model.addAttribute("page", orders.adminList(status, page));
        model.addAttribute("status", status);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("active", "orders");
        model.addAttribute("order", orders.adminDetail(id));
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order-detail";
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id, @RequestParam OrderStatus status, RedirectAttributes ra) {
        orders.changeStatus(id, status);
        ra.addFlashAttribute("message", "주문 상태를 변경했습니다.");
        return "redirect:/admin/orders/" + id;
    }
}
