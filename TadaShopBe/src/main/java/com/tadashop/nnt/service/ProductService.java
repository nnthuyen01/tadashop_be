package com.tadashop.nnt.service;

import java.util.List;

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
	
	List<ProductBriefDto> getProducts();
	
	Page<ProductBriefDto> getProductByLeague(String name, Pageable pageable);

	List<ProductBriefDto> getProductsByQueryName(String name);
	
}
