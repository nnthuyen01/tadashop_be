package com.tadashop.nnt.dto;

import java.io.Serializable;
import java.util.List;

import com.tadashop.nnt.utils.constant.ProductStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProductDto implements Serializable{
	private Long id;
	
	@NotEmpty(message = "Name id required")
	private String name;
	
	@Min(value = 0)
	private Double price;
	
	@Min(value = 0)
	private Integer quantity;
	
	private Boolean isFeatured;
	
	@Min(value = 0)
	@Max(value = 100)
	private Float discount;
	
	private Long viewCount;
	
	private String brief;
	private String description;

	private ProductStatus status; 
	
	private Long clubId;
	private Long brandId;
	
	private List<ProductImageDto> images;
	private ProductImageDto image;
	
	private ClubDto club;
	private BrandDto brand;
}