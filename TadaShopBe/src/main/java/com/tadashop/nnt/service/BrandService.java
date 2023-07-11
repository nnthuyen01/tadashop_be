package com.tadashop.nnt.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tadashop.nnt.dto.BrandDto;
import com.tadashop.nnt.model.Brand;

public interface BrandService {
	Brand insertBrand(BrandDto dto);

	List<?> findAll();

	Page<Brand> findAll(Pageable pageable);

	Page<Brand> findByName(String name, Pageable pageable);

	Brand findById(Long id);

	void deleteId(Long id);

	Brand updateBrand(Long id, BrandDto dto);
}
