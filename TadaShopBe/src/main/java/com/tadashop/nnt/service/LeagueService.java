package com.tadashop.nnt.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tadashop.nnt.model.League;

public interface LeagueService {

	League save(League entity);
	
	League update(Long id, League entity);
	
	List<League> findAll();
	
	Page<League> findAll(Pageable pageale );
	
	League findById(Long id);
	
	void deleteById(Long id);
}
