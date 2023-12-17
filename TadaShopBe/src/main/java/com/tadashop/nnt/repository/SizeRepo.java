package com.tadashop.nnt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.model.Size;

public interface SizeRepo extends JpaRepository<Size, Long>{
	
    boolean existsById(Long id);
    List<Size> findAllByProduct(Product product);
    
    List<Size> findSizeByProductId(Long id);
    
    List<Size> findAllByIdIn(List<Long> productSizeId);
    
	List<Size> findBySizeContainsIgnoreCaseAndProduct(String size, Product product);
	List<Size> findBySizeAndProduct(String size, Product product);
	
//	Page<Size> findBySizeContainsIgnoreCase(String size, Pageable pageable);
	
	@Query("select s from Size s where lower(s.size) like lower(concat('%', :size, '%'))")
	Page<Size> findBySizeContainsIgnoreCase(@Param("size") String size, Pageable pageable);
	
	@Query("select s from Size s where lower(s.size) like lower(concat('%', :query, '%')) or lower(s.product.name) like lower(concat('%', :query, '%'))")
	Page<Size> findBySizeOrProductNameContainsIgnoreCase(@Param("query") String query, Pageable pageable);

}
