package com.cmsstarter.domain.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = { "items", "member" })
    Optional<Order> findByOrderNo(String orderNo);

    @EntityGraph(attributePaths = { "items", "member" })
    Optional<Order> findWithItemsById(Long id);

    @Query(value = "select o from ShopOrder o join fetch o.member", countQuery = "select count(o) from ShopOrder o")
    Page<Order> findAllWithMember(Pageable pageable);

    @Query(value = "select o from ShopOrder o join fetch o.member where o.status = :status",
            countQuery = "select count(o) from ShopOrder o where o.status = :status")
    Page<Order> findByStatusWithMember(OrderStatus status, Pageable pageable);

    @Query("select o from ShopOrder o join fetch o.items where o.member.email = :email order by o.id desc")
    List<Order> findByMemberEmail(String email);

    @Query("select coalesce(sum(o.total), 0) from ShopOrder o where o.paidAt >= :from and o.status <> com.cmsstarter.domain.order.OrderStatus.CANCELED")
    long sumPaidSince(LocalDateTime from);

    long countByCreatedAtGreaterThanEqual(LocalDateTime from);

    List<Order> findTop5ByOrderByIdDesc();
}
