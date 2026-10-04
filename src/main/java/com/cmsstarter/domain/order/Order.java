package com.cmsstarter.domain.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cmsstarter.domain.member.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "ShopOrder")
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "items_total", nullable = false)
    private int itemsTotal;

    @Column(name = "shipping_fee", nullable = false)
    private int shippingFee;

    @Column(nullable = false)
    private int total;

    @Column(name = "pay_method", nullable = false)
    private String payMethod;

    @Column(name = "imp_uid")
    private String impUid;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false)
    private String receiverPhone;

    private String zipcode;

    @Column(nullable = false)
    private String address;

    private String memo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }

    /** 목록 표시용: "오버핏 울 코트 외 1" */
    public String getSummary() {
        if (items.isEmpty()) {
            return "-";
        }
        String first = items.get(0).getProductName();
        return items.size() > 1 ? first + " 외 " + (items.size() - 1) : first;
    }
}
