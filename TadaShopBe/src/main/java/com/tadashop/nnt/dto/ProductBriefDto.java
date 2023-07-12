package com.tadashop.nnt.dto;


import java.io.Serializable;

import com.tadashop.nnt.utils.constant.ProductStatus;

import lombok.Data;

@Data
public class ProductBriefDto implements Serializable{
	private Long id;
	private String name;
	private Integer quantity;	
	private Double price;
	private Float discount;
	
	private Boolean isFeatured;
	private String brief;

	private ProductStatus status; 	
	private String clubName;
	private String brandName;	
	private String imageFileName;
}
