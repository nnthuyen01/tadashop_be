package com.tadashop.nnt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.Brand;

@Repository
public interface BrandRepo extends JpaRepository<Brand, Long>{
	List<Brand> findByNameContainsIgnoreCase(String name);
	
	
	Page<Brand> findByNameContainsIgnoreCase(String name, Pageable pageable);
	
	List<Brand> findByIdNotAndNameContainsIgnoreCase(Long id, String name);
}
