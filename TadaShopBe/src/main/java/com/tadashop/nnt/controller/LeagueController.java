package com.tadashop.nnt.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.dto.LeagueDto;
import com.tadashop.nnt.model.League;
import com.tadashop.nnt.service.LeagueService;
import com.tadashop.nnt.service.iplm.MapValidationErrorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/")
@CrossOrigin
public class LeagueController {
	@Autowired
	LeagueService leagueService;
	@Autowired
	MapValidationErrorService mapValidationErrorService;
	
	@PreAuthorize("hasAuthority('admin:create')")
	@PostMapping("/admin/league")
	public ResponseEntity<?> createLeague(@Valid @RequestBody LeagueDto dto,
											BindingResult result) {
		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);
		
		if (responseEntity != null) {
			return responseEntity;
		}
		
		League entity = new League();
		BeanUtils.copyProperties(dto, entity);
		entity = leagueService.save(entity);
		
		dto.setId(entity.getId());
		
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}
	@PreAuthorize("hasAuthority('admin:update')")
	@PatchMapping("/admin/league/{id}")
	public ResponseEntity<?> updateLeague(@PathVariable("id") Long id, @RequestBody LeagueDto dto) {
		League entity = new League();
		BeanUtils.copyProperties(dto, entity);
		entity = leagueService.update(id, entity);
		
		dto.setId(entity.getId());
		
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}
	
	@GetMapping("/league")
	public ResponseEntity<?> getLeagues(){
		return new ResponseEntity<>(leagueService.findAll(), HttpStatus.OK);
	}
	
	@GetMapping("/league/page")
	public ResponseEntity<?> getLeagues(
			@PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC)
			Pageable pageable){
		return new ResponseEntity<>(leagueService.findAll(pageable), HttpStatus.OK);
	}
	
	@GetMapping("/league/{id}/get")
	public ResponseEntity<?> getLeagues(@PathVariable("id") Long id){
		return new ResponseEntity<>(leagueService.findById(id), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('admin:delete')")
	@DeleteMapping("/admin/league/{id}")
	public ResponseEntity<?> deleteLeague(@PathVariable("id") Long id){
		leagueService.deleteById(id);
		
		return new ResponseEntity<>("League with ID: " + id + " was deleted", HttpStatus.OK);
	}
}
