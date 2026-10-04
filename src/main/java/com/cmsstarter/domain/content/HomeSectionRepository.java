package com.cmsstarter.domain.content;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeSectionRepository extends JpaRepository<HomeSection, Long> {

    List<HomeSection> findAllByOrderBySortOrderAscIdAsc();

    List<HomeSection> findByVisibleTrueOrderBySortOrderAscIdAsc();
}
