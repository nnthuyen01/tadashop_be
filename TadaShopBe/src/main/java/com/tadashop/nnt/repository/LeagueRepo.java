package com.tadashop.nnt.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.League;

@Repository
public interface LeagueRepo extends JpaRepository<League, Long>{
	List<League> findByNameStartsWith(String name, Pageable pageable);
}
