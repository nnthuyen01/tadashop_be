package com.tadashop.nnt.controller;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.tadashop.nnt.dto.ClubDto;
import com.tadashop.nnt.model.Club;
import com.tadashop.nnt.service.ClubService;
import com.tadashop.nnt.service.iplm.MapValidationErrorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/")
@CrossOrigin
public class ClubController {
	@Autowired
	ClubService clubService;
	@Autowired
	MapValidationErrorService mapValidationErrorService;
	
	
	@PostMapping("/admin/club")
	public ResponseEntity<?> createClub(@Valid @RequestBody ClubDto dto,
											BindingResult result) {
		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);
		
		if (responseEntity != null) {
			return responseEntity;
		}
		
		Club entity = new Club();
		BeanUtils.copyProperties(dto, entity);
		entity = clubService.save(entity);
		
		dto.setId(entity.getId());
		
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}
	@PatchMapping("/admin/club/{id}")
	public ResponseEntity<?> updateClub(@PathVariable("id") Long id, @RequestBody ClubDto dto) {
		Club entity = new Club();
		BeanUtils.copyProperties(dto, entity);
		entity = clubService.update(id, entity);
		
		dto.setId(entity.getId());
		
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}
	
	@GetMapping("/club")
	public ResponseEntity<?> getClubs(){
		return new ResponseEntity<>(clubService.findAll(), HttpStatus.OK);
	}
	
	@GetMapping("/club/page")
	public ResponseEntity<?> getClubs(
			@PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC)
			Pageable pageable){
		return new ResponseEntity<>(clubService.findAll(pageable), HttpStatus.OK);
	}
	
	@GetMapping("/club/{id}/get")
	public ResponseEntity<?> getClubs(@PathVariable("id") Long id){
		return new ResponseEntity<>(clubService.findById(id), HttpStatus.OK);
	}
	
	@DeleteMapping("/admin/club/{id}")
	public ResponseEntity<?> deleteClub(@PathVariable("id") Long id){
		clubService.deleteById(id);
		
		return new ResponseEntity<>("Club with ID: " + id + " was deleted", HttpStatus.OK);
	}
}
