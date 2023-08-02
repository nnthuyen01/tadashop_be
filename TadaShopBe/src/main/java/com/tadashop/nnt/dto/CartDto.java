package com.tadashop.nnt.dto;

import com.tadashop.nnt.model.CartID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
	private CartID cartID;
	private int quantity;
}
