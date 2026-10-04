package com.cmsstarter.domain.order;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmsstarter.config.BrandProperties;
import com.cmsstarter.domain.cart.CartItem;
import com.cmsstarter.domain.cart.CartItemRepository;
import com.cmsstarter.domain.catalog.Product;
import com.cmsstarter.domain.catalog.ProductRepository;
import com.cmsstarter.domain.catalog.ProductStatus;
import com.cmsstarter.domain.member.Member;
import com.cmsstarter.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    private final OrderRepository orders;
    private final CartItemRepository carts;
    private final ProductRepository products;
    private final MemberRepository members;
    private final BrandProperties brand;
    private final PortOneClient portOne;

    /** 장바구니로 결제대기 주문을 만든다. 금액은 항상 서버에서 계산한다. */
    @Transactional
    public Order createPending(String email, CheckoutForm form) {
        Member member = members.findByEmail(email).orElseThrow();
        List<CartItem> cart = carts.findByMemberId(member.getId());
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어 있습니다.");
        }
        Order order = new Order();
        order.setOrderNo(newOrderNo());
        order.setMember(member);
        order.setStatus(OrderStatus.PENDING);
        order.setPayMethod(form.getPayMethod());
        order.setReceiverName(form.getReceiverName().trim());
        order.setReceiverPhone(form.getReceiverPhone().trim());
        order.setZipcode(form.getZipcode());
        order.setAddress(form.getAddress().trim());
        order.setMemo(form.getMemo());
        int itemsTotal = 0;
        for (CartItem c : cart) {
            Product p = c.getProduct();
            if (!p.isPurchasable() || p.getStock() < c.getQuantity()) {
                throw new IllegalArgumentException("'" + p.getName() + "' 상품의 재고가 부족합니다.");
            }
            OrderItem oi = new OrderItem();
            oi.setProductId(p.getId());
            oi.setProductName(p.getName());
            oi.setThumbnail(p.getThumbnail());
            oi.setColor(c.getColor());
            oi.setSize(c.getSize());
            oi.setUnitPrice(p.getPrice());
            oi.setQuantity(c.getQuantity());
            order.addItem(oi);
            itemsTotal += oi.getLineTotal();
        }
        order.setItemsTotal(itemsTotal);
        order.setShippingFee(shippingFee(itemsTotal));
        order.setTotal(itemsTotal + order.getShippingFee());
        return orders.save(order);
    }

    public int shippingFee(int itemsTotal) {
        return itemsTotal == 0 || itemsTotal >= brand.getFreeShippingOver() ? 0 : brand.getShippingFee();
    }

    /** 결제 완료 처리. 이미 처리된 주문이면 그대로 반환한다(멱등). */
    @Transactional
    public Order complete(String email, String orderNo, String impUid) {
        Order order = orders.findByOrderNo(orderNo).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        if (!order.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("주문을 찾을 수 없습니다.");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            return order;
        }
        portOne.verify(impUid, orderNo, order.getTotal());
        for (OrderItem item : order.getItems()) {
            Product p = products.findById(item.getProductId()).orElseThrow();
            if (p.getStock() < item.getQuantity()) {
                throw new IllegalStateException("'" + p.getName() + "' 재고가 부족해 주문을 완료할 수 없습니다.");
            }
            p.setStock(p.getStock() - item.getQuantity());
            if (p.getStock() == 0) {
                p.setStatus(ProductStatus.SOLD_OUT);
            }
        }
        order.setStatus(OrderStatus.PAID);
        order.setImpUid(impUid);
        order.setPaidAt(LocalDateTime.now());
        carts.deleteByMemberId(order.getMember().getId());
        return order;
    }

    @Transactional(readOnly = true)
    public Order findForMember(String email, String orderNo) {
        Order order = orders.findByOrderNo(orderNo).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        if (!order.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("주문을 찾을 수 없습니다.");
        }
        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> myOrders(String email) {
        return orders.findByMemberEmail(email);
    }

    @Transactional(readOnly = true)
    public Page<Order> adminList(OrderStatus status, int page) {
        PageRequest pr = PageRequest.of(Math.max(page, 0), 20, Sort.by("id").descending());
        return status == null ? orders.findAllWithMember(pr) : orders.findByStatusWithMember(status, pr);
    }

    @Transactional(readOnly = true)
    public Order adminDetail(Long id) {
        return orders.findWithItemsById(id).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }

    @Transactional
    public void changeStatus(Long id, OrderStatus next) {
        Order order = orders.findWithItemsById(id).orElseThrow();
        OrderStatus prev = order.getStatus();
        if (prev == next) {
            return;
        }
        boolean wasPaid = prev != OrderStatus.PENDING && prev != OrderStatus.CANCELED;
        if (next == OrderStatus.CANCELED && wasPaid) {
            for (OrderItem item : order.getItems()) {
                products.findById(item.getProductId()).ifPresent(p -> {
                    p.setStock(p.getStock() + item.getQuantity());
                    if (p.getStatus() == ProductStatus.SOLD_OUT) {
                        p.setStatus(ProductStatus.ON_SALE);
                    }
                });
            }
        }
        order.setStatus(next);
    }

    @Transactional(readOnly = true)
    public Dashboard dashboard() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        return new Dashboard(
                orders.sumPaidSince(start),
                orders.countByCreatedAtGreaterThanEqual(start),
                products.countByStatusNot(ProductStatus.HIDDEN),
                products.countLowStock(5),
                orders.findTop5ByOrderByIdDesc());
    }

    public record Dashboard(long todaySales, long newOrders, long productCount, long lowStock, List<Order> recent) {
    }

    private String newOrderNo() {
        StringBuilder sb = new StringBuilder(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now())).append('-');
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
