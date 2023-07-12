package com.tadashop.nnt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tadashop.nnt.dto.ProductBriefDto;
import com.tadashop.nnt.dto.ProductDetailResp;
import com.tadashop.nnt.dto.ProductDto;

public interface ProductService {

	ProductDto insertProduct(ProductDto dto);
	
	ProductDto updateProduct(Long id, ProductDto dto);
	
	Page<ProductBriefDto> getProductBriefsByName(String name, Pageable pageable);
	
	void deleteProductById(Long id);
	
	ProductDto getEditedProductById(Long id);
	
	ProductDetailResp findProductById(Long id);
}
