package com.tadashop.nnt.service.iplm;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.League;
import com.tadashop.nnt.repository.LeagueRepo;
import com.tadashop.nnt.service.LeagueService;

@Service
public class LeagueIplm implements LeagueService {
	@Autowired
	private LeagueRepo leagueRepository;

	public League save(League entity) {
		return leagueRepository.save(entity);
	}

	public League update(Long id, League entity) {
		Optional<League> existed = leagueRepository.findById(id);

		if (existed.isEmpty()) {
			throw new AppException("League id " + id + " does not exist");
		}

		try {
			League existedLeague = existed.get();
			existedLeague.setName(entity.getName());
			return leagueRepository.save(existedLeague);
		} catch (Exception ex) {
			throw new AppException("League is updated fail");
		}
	}

	public List<League> findAll() {
		return leagueRepository.findAll();
	}
	
	public Page<League> findAll(Pageable pageable) {
		return leagueRepository.findAll(pageable);
	}
	
	public League findById(Long id) {
		Optional<League> found =  leagueRepository.findById(id);
		
		if(found.isEmpty()) {
			throw new AppException("League with id " + id + " does not exist");
		}
		
		return found.get();
	}
	
	public void deleteById(Long id) {
		League existed = findById(id);
		
		leagueRepository.delete(existed);
	}
}