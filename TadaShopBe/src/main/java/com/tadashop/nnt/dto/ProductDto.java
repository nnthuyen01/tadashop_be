package com.tadashop.nnt.dto;

import java.io.Serializable;
import java.util.List;

import com.tadashop.nnt.utils.constant.ProductGender;
import com.tadashop.nnt.utils.constant.ProductKitType;
import com.tadashop.nnt.utils.constant.ProductStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductDto implements Serializable{
	
	private Long id;
	
	@NotEmpty(message = "Name id required")
	private String name;
	
	@Min(value = 0)
	private Double originalPrice;

	private Double priceAfterDiscount;
	
	private Integer totalQuantity;
	
	private Boolean isFeatured;
	
	@Min(value = 0)
	@Max(value = 100)
	private Float discount;

	@Size(max = 500, message = "Brief cannot exceed 500 characters")
	private String brief;
	@Size(max = 2000, message = "Description cannot exceed 2000 characters")
	private String description;

	private ProductGender gender;
	private ProductKitType kitType;
	private String season;
	private ProductStatus status; 
	
	private Long clubId;
	private Long brandId;
	
	private List<ProductImageDto> images;
	private ProductImageDto image;
	
	private ClubDto club;
	private BrandDto brand;
}