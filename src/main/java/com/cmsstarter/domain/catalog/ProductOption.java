package com.cmsstarter.domain.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_option")
@Getter
@Setter
@NoArgsConstructor
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OptionType type;

    @Column(nullable = false)
    private String value;

    @Column(name = "color_hex")
    private String colorHex;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
