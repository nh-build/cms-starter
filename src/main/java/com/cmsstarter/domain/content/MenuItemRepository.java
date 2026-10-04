package com.cmsstarter.domain.content;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("select m from MenuItem m left join fetch m.category where m.visible = true order by m.sortOrder, m.id")
    List<MenuItem> findVisibleWithCategory();

    @Query("select m from MenuItem m left join fetch m.category order by m.sortOrder, m.id")
    List<MenuItem> findAllWithCategory();

    long countByCategoryId(Long categoryId);

    @Query("select coalesce(max(m.sortOrder), 0) from MenuItem m")
    int maxSortOrder();
}
