package com.tadashop.nnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.ProductImage;

@Repository
public interface ProductImageRepo extends JpaRepository<ProductImage, Long>{

}
