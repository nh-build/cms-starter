package com.cmsstarter.domain.catalog;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnail;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ON_SALE;

    @Column(nullable = false)
    private boolean featured;

    @Column(name = "is_new", nullable = false)
    private boolean newArrival;

    @Column(name = "is_best", nullable = false)
    private boolean best;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder, id")
    private Set<ProductImage> images = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder, id")
    private Set<ProductOption> options = new LinkedHashSet<>();

    public Set<ProductOption> optionsOf(OptionType type) {
        return options.stream().filter(o -> o.getType() == type).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public boolean isPurchasable() {
        return status == ProductStatus.ON_SALE && stock > 0;
    }
}
