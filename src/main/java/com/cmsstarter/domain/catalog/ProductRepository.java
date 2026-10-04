package com.cmsstarter.domain.catalog;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = { "images", "options", "category" })
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findDetailById(Long id);

    List<Product> findByFeaturedTrueAndStatusNotOrderByIdDesc(ProductStatus status, Pageable pageable);

    List<Product> findByNewArrivalTrueAndStatusNotOrderByIdDesc(ProductStatus status, Pageable pageable);

    List<Product> findByBestTrueAndStatusNotOrderByIdDesc(ProductStatus status, Pageable pageable);

    long countByStatusNot(ProductStatus status);

    @Query("select count(p) from Product p where p.status = com.cmsstarter.domain.catalog.ProductStatus.ON_SALE and p.stock <= :threshold")
    long countLowStock(int threshold);

    long countByCategoryId(Long categoryId);
}
