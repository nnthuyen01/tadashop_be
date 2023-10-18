package com.tadashop.nnt.service.iplm;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.dto.ClubDto;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Club;
import com.tadashop.nnt.model.League;
import com.tadashop.nnt.model.User;
import com.tadashop.nnt.repository.ClubRepo;
import com.tadashop.nnt.repository.LeagueRepo;
import com.tadashop.nnt.service.ClubService;

@Service
public class ClubIplm implements ClubService {
	@Autowired
	private ClubRepo clubRepository;
	@Autowired
	private LeagueRepo leagueRepository;

	public Club save(ClubDto dto) {

		Club entity = new Club();
		BeanUtils.copyProperties(dto, entity);
		var league = leagueRepository.getReferenceById(dto.getLeagueId());
		if (!league.equals(null)) {

			entity.setLeague(league);
			return clubRepository.save(entity);
		} else
			throw new AppException("League id " + dto.getLeagueId() + " does not exist");
	}

	public Club update(Long id, ClubDto dto) {
		Optional<Club> existed = clubRepository.findById(id);

		if (existed.isEmpty()) {
			throw new AppException("Club id " + id + " does not exist");
		}

		try {
			Club existedClub = existed.get();
			existedClub.setName(dto.getName());
			if (dto.getLeagueId() != null) {
				var league = leagueRepository.getReferenceById(dto.getLeagueId());
				if (!league.equals(null)) {

					existedClub.setLeague(league);
					return clubRepository.save(existedClub);
				} else
					throw new AppException("League id " + dto.getLeagueId() + " does not exist");
			}
			return clubRepository.save(existedClub);
		} catch (Exception ex) {
			throw new AppException("Club is updated fail");
		}
	}

	public List<Club> findAll() {
		return clubRepository.findAll();
	}

	public Page<Club> findAll(Pageable pageable) {
		return clubRepository.findAll(pageable);
	}

	public Club findById(Long id) {
		Optional<Club> found = clubRepository.findById(id);

		if (found.isEmpty()) {
			throw new AppException("Club with id " + id + " does not exist");
		}

		return found.get();
	}

	public void deleteById(Long id) {
		Club existed = findById(id);

		clubRepository.delete(existed);
	}
}
