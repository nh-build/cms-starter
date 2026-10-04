package com.cmsstarter.domain.content;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findAllByOrderBySortOrderAscIdAsc();

    List<Banner> findByVisibleTrueOrderBySortOrderAscIdAsc();
}
