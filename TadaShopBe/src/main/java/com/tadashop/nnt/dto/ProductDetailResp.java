package com.tadashop.nnt.dto;

import java.io.Serializable;
import java.util.List;

import com.tadashop.nnt.model.Brand;
import com.tadashop.nnt.model.Club;
import com.tadashop.nnt.model.Size;
import com.tadashop.nnt.utils.constant.ProductGender;
import com.tadashop.nnt.utils.constant.ProductKitType;
import com.tadashop.nnt.utils.constant.ProductStatus;


import lombok.Data;

@Data
public class ProductDetailResp implements Serializable{
	
	private Long id;
	private String name;
	private Double price;
//	private Integer quantity;
	private Integer totalQuantity;
	private Boolean isFeatured;
	private Float discount;
	private String brief;
	private String description;
	private ProductGender gender;
	private ProductKitType kitType;
	private String season;
	private ProductStatus status; 
	private List<ProductImageDto> images;
	private List<Size> sizes;
	private ProductImageDto image;
	private Club club;
	private Brand brand;
}