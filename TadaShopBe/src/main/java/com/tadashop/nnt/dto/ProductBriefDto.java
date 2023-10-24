package com.tadashop.nnt.dto;


import java.io.Serializable;

import com.tadashop.nnt.utils.constant.ProductGender;
import com.tadashop.nnt.utils.constant.ProductKitType;
import com.tadashop.nnt.utils.constant.ProductStatus;

import lombok.Data;

@Data
public class ProductBriefDto implements Serializable{
	private Long id;
	private String name;
	private Integer totalQuantity;
	private Double originalPrice;
	private Double priceAfterDiscount;
	private Float discount;
	
	private Boolean isFeatured;
	private String brief;

	private ProductGender gender;
	private ProductKitType kitType;
	private String season;
	private ProductStatus status; 	
	private String clubName;
	private String brandName;	
	private String imageFileName;
}
