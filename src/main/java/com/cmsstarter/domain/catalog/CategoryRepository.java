package com.cmsstarter.domain.catalog;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderBySortOrderAscIdAsc();

    List<Category> findByVisibleTrueOrderBySortOrderAscIdAsc();

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
