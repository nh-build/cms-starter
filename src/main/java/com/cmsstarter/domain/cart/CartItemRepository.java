package com.cmsstarter.domain.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select c from CartItem c join fetch c.product where c.member.id = :memberId order by c.id")
    List<CartItem> findByMemberId(Long memberId);

    Optional<CartItem> findByIdAndMemberId(Long id, Long memberId);

    @Query("select c from CartItem c where c.member.id = :memberId and c.product.id = :productId "
            + "and ((:color is null and c.color is null) or c.color = :color) "
            + "and ((:size is null and c.size is null) or c.size = :size)")
    Optional<CartItem> findSame(Long memberId, Long productId, String color, String size);

    @Query("select coalesce(sum(c.quantity), 0) from CartItem c where c.member.id = :memberId")
    int countQuantity(Long memberId);

    void deleteByMemberId(Long memberId);
}
