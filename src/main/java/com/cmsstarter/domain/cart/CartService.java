package com.cmsstarter.domain.cart;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmsstarter.domain.catalog.OptionType;
import com.cmsstarter.domain.catalog.Product;
import com.cmsstarter.domain.catalog.ProductRepository;
import com.cmsstarter.domain.member.Member;
import com.cmsstarter.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final int MAX_QTY = 99;

    private final CartItemRepository carts;
    private final ProductRepository products;
    private final MemberRepository members;

    @Transactional(readOnly = true)
    public List<CartItem> items(String email) {
        return carts.findByMemberId(memberId(email));
    }

    @Transactional(readOnly = true)
    public int count(String email) {
        return members.findByEmail(email).map(m -> carts.countQuantity(m.getId())).orElse(0);
    }

    @Transactional
    public void add(String email, Long productId, String color, String size, int qty) {
        Member member = members.findByEmail(email).orElseThrow();
        Product product = products.findDetailById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        if (!product.isPurchasable()) {
            throw new IllegalArgumentException("현재 구매할 수 없는 상품입니다.");
        }
        String c = blankToNull(color);
        String s = blankToNull(size);
        if (!product.optionsOf(OptionType.COLOR).isEmpty() && c == null) {
            throw new IllegalArgumentException("색상을 선택하세요.");
        }
        if (!product.optionsOf(OptionType.SIZE).isEmpty() && s == null) {
            throw new IllegalArgumentException("사이즈를 선택하세요.");
        }
        validateOption(product, OptionType.COLOR, c);
        validateOption(product, OptionType.SIZE, s);
        int add = Math.max(1, Math.min(qty, MAX_QTY));
        CartItem item = carts.findSame(member.getId(), productId, c, s).orElseGet(() -> {
            CartItem n = new CartItem();
            n.setMember(member);
            n.setProduct(product);
            n.setColor(c);
            n.setSize(s);
            n.setQuantity(0);
            return n;
        });
        item.setQuantity(Math.min(item.getQuantity() + add, Math.min(MAX_QTY, product.getStock())));
        carts.save(item);
    }

    @Transactional
    public void updateQuantity(String email, Long itemId, int qty) {
        CartItem item = carts.findByIdAndMemberId(itemId, memberId(email)).orElseThrow();
        if (qty <= 0) {
            carts.delete(item);
            return;
        }
        item.setQuantity(Math.min(qty, Math.min(MAX_QTY, Math.max(item.getProduct().getStock(), 1))));
    }

    @Transactional
    public void remove(String email, Long itemId) {
        carts.findByIdAndMemberId(itemId, memberId(email)).ifPresent(carts::delete);
    }

    @Transactional
    public void clear(String email) {
        carts.deleteByMemberId(memberId(email));
    }

    private Long memberId(String email) {
        return members.findByEmail(email).orElseThrow().getId();
    }

    private void validateOption(Product product, OptionType type, String value) {
        if (value != null && product.optionsOf(type).stream().noneMatch(o -> o.getValue().equals(value))) {
            throw new IllegalArgumentException("올바르지 않은 옵션입니다.");
        }
    }

    private static String blankToNull(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }
}
