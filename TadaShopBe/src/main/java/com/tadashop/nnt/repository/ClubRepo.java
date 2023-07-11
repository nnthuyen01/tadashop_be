package com.tadashop.nnt.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tadashop.nnt.model.Club;

import java.util.List;

public interface ClubRepo extends JpaRepository<Club, Long>{
	List<Club> findByNameStartsWith(String name, Pageable pageable);
}
