package com.cmsstarter.domain.catalog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    public static final int PAGE_SIZE = 12;

    private final ProductRepository products;

    /** 스토어프론트 목록. category 는 slug, sort: new | best | price_asc | price_desc | (기본 최신순) */
    @Transactional(readOnly = true)
    public Page<Product> search(String categorySlug, String sort, String keyword, int page) {
        Specification<Product> spec = (root, q, cb) -> cb.notEqual(root.get("status"), ProductStatus.HIDDEN);
        if (categorySlug != null && !categorySlug.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.join("category").get("slug"), categorySlug));
        }
        if ("new".equals(sort)) {
            spec = spec.and((root, q, cb) -> cb.isTrue(root.get("newArrival")));
        } else if ("best".equals(sort)) {
            spec = spec.and((root, q, cb) -> cb.isTrue(root.get("best")));
        }
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim().toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("name")), like));
        }
        Sort order = switch (sort == null ? "" : sort) {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            default -> Sort.by("id").descending();
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, order);
        return products.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> featured(int limit) {
        return products.findByFeaturedTrueAndStatusNotOrderByIdDesc(ProductStatus.HIDDEN, PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<Product> newest(int limit) {
        return products.findByNewArrivalTrueAndStatusNotOrderByIdDesc(ProductStatus.HIDDEN, PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<Product> best(int limit) {
        return products.findByBestTrueAndStatusNotOrderByIdDesc(ProductStatus.HIDDEN, PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public Product detail(Long id) {
        return products.findDetailById(id).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Page<Product> adminList(int page) {
        return products.findAll(PageRequest.of(Math.max(page, 0), 20, Sort.by("id").descending()));
    }

    public static List<String> splitValues(String csv) {
        List<String> out = new ArrayList<>();
        if (csv == null) {
            return out;
        }
        for (String s : csv.split(",")) {
            String t = s.trim();
            if (!t.isEmpty() && !out.contains(t)) {
                out.add(t);
            }
        }
        return out;
    }
}
