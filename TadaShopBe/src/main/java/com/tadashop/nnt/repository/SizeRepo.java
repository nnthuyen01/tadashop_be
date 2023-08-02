package com.tadashop.nnt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.model.Size;

public interface SizeRepo extends JpaRepository<Size, Long>{
	
    boolean existsById(Long id);
    List<Size> findAllByProduct(Product product);
    
    List<Size> findSizeByProductId(Long id);
    
    List<Size> findAllByIdIn(List<Long> productSizeId);
}
