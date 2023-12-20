package com.tadashop.nnt.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.Brand;
import com.tadashop.nnt.model.Club;

import java.util.List;

@Repository
public interface ClubRepo extends JpaRepository<Club, Long>{
	List<Club> findByNameStartsWith(String name, Pageable pageable);
	
	 List<Club> findByNameIgnoreCase(String name);
}
