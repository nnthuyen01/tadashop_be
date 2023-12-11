package com.tadashop.nnt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.utils.constant.ProductKitType;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

	Page<Product> findByNameContainsIgnoreCase(String name, Pageable pageable);

	List<Product> findByNameContainsIgnoreCase(String name);

	@Query("SELECT p FROM Product p JOIN p.club c JOIN c.league l WHERE l.name = :league")
	Page<Product> findByLeague(@Param("league") String league, Pageable pageable);

	List<Product> findByIdNot(Long idProduct);

	@Query("SELECT p FROM Product p WHERE " + "(p.brand.name IN :brand) AND "
			+ "(CAST(p.kitType AS text) IN :kitType) AND "
			+ "(CAST(p.gender AS text) IN :gender) AND " + "(:minPrice IS NULL OR p.priceAfterDiscount >= :minPrice) AND "
			+ "(:maxPrice IS NULL OR p.priceAfterDiscount <= :maxPrice)")
	Page<Product> findByFilters(@Param("brand") List<String> brand, @Param("kitType") List<String> kitType,
			@Param("gender") List<String> gender, @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);

	
	
	
}
