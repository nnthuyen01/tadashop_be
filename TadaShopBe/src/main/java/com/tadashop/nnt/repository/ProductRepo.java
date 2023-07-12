package com.tadashop.nnt.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.Product;


@Repository
public interface ProductRepo extends JpaRepository<Product, Long>{
	
	Page<Product> findByNameContainsIgnoreCase(String name, Pageable pageable);
}
