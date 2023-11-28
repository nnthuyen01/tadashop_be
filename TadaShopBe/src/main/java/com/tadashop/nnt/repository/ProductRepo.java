package com.tadashop.nnt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

	Page<Product> findByNameContainsIgnoreCase(String name, Pageable pageable);
	
	List<Product> findByNameContainsIgnoreCase(String name);

	@Query("SELECT p FROM Product p JOIN p.club c JOIN c.league l WHERE l.name = :league")
	Page<Product> findByLeague(@Param("league") String league, Pageable pageable);

}
