package com.cmsstarter.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    private String thumbnail;

    private String color;

    private String size;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    @Column(nullable = false)
    private int quantity;

    public int getLineTotal() {
        return unitPrice * quantity;
    }

    public String getOptionLabel() {
        StringBuilder sb = new StringBuilder();
        if (color != null && !color.isBlank()) {
            sb.append(color);
        }
        if (size != null && !size.isBlank()) {
            sb.append(sb.length() > 0 ? " / " : "").append(size);
        }
        return sb.toString();
    }
}
