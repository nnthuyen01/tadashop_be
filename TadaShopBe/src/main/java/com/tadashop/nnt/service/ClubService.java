package com.tadashop.nnt.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tadashop.nnt.model.Club;

public interface ClubService {
	
	Club save(Club entity);
	
	Club update(Long id, Club entity);
	
	List<Club> findAll();
	
	Page<Club> findAll(Pageable pageale );
	
	Club findById(Long id);
	
	void deleteById(Long id);
}	