package com.cmsstarter.web.shop;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmsstarter.config.BrandProperties;
import com.cmsstarter.domain.cart.CartItem;
import com.cmsstarter.domain.cart.CartService;
import com.cmsstarter.domain.order.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cart;
    private final OrderService orders;
    private final BrandProperties brand;

    @GetMapping("/cart")
    public String view(Principal principal, Model model) {
        List<CartItem> items = cart.items(principal.getName());
        int itemsTotal = items.stream().mapToInt(CartItem::getLineTotal).sum();
        int shipping = orders.shippingFee(itemsTotal);
        model.addAttribute("items", items);
        model.addAttribute("itemsTotal", itemsTotal);
        model.addAttribute("shippingFee", shipping);
        model.addAttribute("total", itemsTotal + shipping);
        return "shop/cart";
    }

    @PostMapping("/cart/add")
    public String add(Principal principal,
                      @RequestParam Long productId,
                      @RequestParam(required = false) String color,
                      @RequestParam(required = false) String size,
                      @RequestParam(defaultValue = "1") int quantity,
                      @RequestParam(defaultValue = "false") boolean buyNow,
                      RedirectAttributes ra) {
        try {
            cart.add(principal.getName(), productId, color, size, quantity);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/" + productId;
        }
        return buyNow ? "redirect:/checkout" : "redirect:/cart";
    }

    @PostMapping("/cart/{id}/quantity")
    public String quantity(Principal principal, @PathVariable Long id, @RequestParam int quantity) {
        cart.updateQuantity(principal.getName(), id, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/{id}/delete")
    public String delete(Principal principal, @PathVariable Long id) {
        cart.remove(principal.getName(), id);
        return "redirect:/cart";
    }
}
