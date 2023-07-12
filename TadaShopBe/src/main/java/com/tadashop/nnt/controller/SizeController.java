package com.tadashop.nnt.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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


import com.tadashop.nnt.dto.SizeDto;

import com.tadashop.nnt.model.Size;

import com.tadashop.nnt.service.SizeService;
import com.tadashop.nnt.service.iplm.MapValidationErrorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/")
@CrossOrigin
public class SizeController {
	@Autowired
	SizeService sizeService;
	@Autowired
	MapValidationErrorService mapValidationErrorService;
	
	@PreAuthorize("hasAuthority('admin:create')")
	@PostMapping("/admin/size")
	public ResponseEntity<?> createSize(@Valid @RequestBody SizeDto dto,
											BindingResult result) {
		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);
		
		if (responseEntity != null) {
			return responseEntity;
		}
		
		Size entity = sizeService.saveSize(dto);
		
		dto.setId(entity.getId());
		
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}
	
	
	@PreAuthorize("hasAuthority('admin:update')")
	@PatchMapping("admin/size/{id}")
	public ResponseEntity<?> updateSize(@PathVariable Long id, @Valid @RequestBody SizeDto dto, BindingResult result) {
		System.out.println("update product");
		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);

		if (responseEntity != null) {
			return responseEntity;
		}
		var updateDto = sizeService.updateSize(id, dto);
		
		return new ResponseEntity<>(updateDto, HttpStatus.CREATED);
	}
	
	
	@DeleteMapping("/size/{id}")
	public ResponseEntity<?> deleteSize(@PathVariable Long id) {
		sizeService.deleteSizeById(id);
		return new ResponseEntity<>("Size with ID: " + id + " was deleted", HttpStatus.OK);
	}
	@GetMapping("/size/{id}")
	public ResponseEntity<?> getSizeById(@PathVariable Long id) {

		return new ResponseEntity<>(sizeService.getSizeById(id), HttpStatus.OK);
	}
	@GetMapping("/size/all/{id}")
	public ResponseEntity<?> getSizesByIdProduct(@PathVariable Long id) {

		return new ResponseEntity<>(sizeService.findAllSizeByProduct(id), HttpStatus.OK);
	}
}