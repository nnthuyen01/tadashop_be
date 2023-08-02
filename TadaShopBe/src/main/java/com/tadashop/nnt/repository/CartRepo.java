package com.tadashop.nnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tadashop.nnt.model.Cart;
import com.tadashop.nnt.model.CartID;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<Cart, CartID> {

    /**
     * findByIdUser_id -> directive "findBy" field "CartID.user_id"
     * findByIdProduct_id -> directive "findBy" field "CartID.product_id"
     * Search Spring Boot Composite Key for more references
     */
    List<Cart> findByCartID_UserId(Long user_id);

    List<Cart> findByCartID_ProductSizeId(Long productSizeId);

    @Modifying
    @Transactional
    Integer deleteByCartID(CartID id);

    @Query("select c from Cart c where c.cartID.userId = :userId and c.cartID.productSizeId = :productSizeId")
    Cart findCartByUserIdAndProductSizeId(Long userId,Long productSizeId);

    void deleteAllByCartID_UserId(Long userId);
}