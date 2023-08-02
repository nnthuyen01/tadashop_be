package com.tadashop.nnt.service;

import java.util.List;

import com.tadashop.nnt.dto.CartResp;
import com.tadashop.nnt.model.Cart;
import com.tadashop.nnt.model.CartID;

public interface CartService {
    void save(Long productSizeId, int quantity);
    Cart updateCart(CartID cartID, int quantity);

    List<CartResp> view(Long userID);

    Integer deleteCart(CartID cartID);
    void deleteAllCartUser();

    Cart increase(CartID cartID);
    Cart decrease(CartID cartID);

}
