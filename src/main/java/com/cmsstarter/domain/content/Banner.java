package com.cmsstarter.domain.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eyebrow;

    @Column(nullable = false)
    private String title;

    private String subtitle;

    @Column(name = "cta_label")
    private String ctaLabel;

    @Column(name = "cta_url")
    private String ctaUrl;

    private String image;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    private boolean visible = true;
}
