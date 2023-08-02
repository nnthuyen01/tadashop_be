package com.tadashop.nnt.dto;

import com.tadashop.nnt.model.ProductImage;
import com.tadashop.nnt.model.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CartResp {

	private Integer quantity;
	private Items item;

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class Items {
		Size size;
		String productName;
		ProductImage image;
		Double price;
	}
}