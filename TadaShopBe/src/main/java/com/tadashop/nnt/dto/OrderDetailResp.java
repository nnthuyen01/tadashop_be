package com.tadashop.nnt.dto;

import java.util.List;

import com.tadashop.nnt.model.Order;
import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.model.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OrderDetailResp {
    private Order order;
    private List<Items> items;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Items{

        Size productSize;
        String itemName;
        Product product;
        Integer quantity;
        Double totalPrice;
    }

}
