package com.cmsstarter.web.shop;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmsstarter.domain.cart.CartItem;
import com.cmsstarter.domain.cart.CartService;
import com.cmsstarter.domain.member.Member;
import com.cmsstarter.domain.member.MemberRepository;
import com.cmsstarter.domain.order.CheckoutForm;
import com.cmsstarter.domain.order.Order;
import com.cmsstarter.domain.order.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** 주문/결제: prepare(주문 생성) → 포트원 결제창 → complete(서버 검증) 순서. */
@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cart;
    private final OrderService orders;
    private final MemberRepository members;

    @GetMapping("/checkout")
    public String checkout(Principal principal, Model model) {
        List<CartItem> items = cart.items(principal.getName());
        if (items.isEmpty()) {
            return "redirect:/cart";
        }
        int itemsTotal = items.stream().mapToInt(CartItem::getLineTotal).sum();
        int shipping = orders.shippingFee(itemsTotal);
        Member me = members.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("items", items);
        model.addAttribute("itemsTotal", itemsTotal);
        model.addAttribute("shippingFee", shipping);
        model.addAttribute("total", itemsTotal + shipping);
        model.addAttribute("me", me);
        return "shop/checkout";
    }

    @PostMapping("/checkout/prepare")
    @ResponseBody
    public ResponseEntity<?> prepare(Principal principal, @Valid @RequestBody CheckoutForm form) {
        try {
            Order order = orders.createPending(principal.getName(), form);
            String name = order.getItems().size() > 1
                    ? order.getItems().get(0).getProductName() + " 외 " + (order.getItems().size() - 1) + "건"
                    : order.getItems().get(0).getProductName();
            return ResponseEntity.ok(Map.of("orderNo", order.getOrderNo(), "amount", order.getTotal(), "name", name,
                    "payMethod", order.getPayMethod()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/checkout/complete")
    @ResponseBody
    public ResponseEntity<?> complete(Principal principal, @RequestBody Map<String, String> body) {
        try {
            Order order = orders.complete(principal.getName(), body.get("merchantUid"), body.get("impUid"));
            return ResponseEntity.ok(Map.of("redirect", "/orders/" + order.getOrderNo()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public String myOrders(Principal principal, Model model) {
        model.addAttribute("orders", orders.myOrders(principal.getName()));
        return "shop/orders";
    }

    @GetMapping("/orders/{orderNo}")
    public String orderDone(Principal principal, @PathVariable String orderNo, Model model) {
        model.addAttribute("order", orders.findForMember(principal.getName(), orderNo));
        return "shop/order-detail";
    }
}
