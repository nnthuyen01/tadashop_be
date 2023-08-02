package com.tadashop.nnt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.tadashop.nnt.dto.VoucherDto;
import com.tadashop.nnt.model.Voucher;
import com.tadashop.nnt.repository.VoucherRepo;
import com.tadashop.nnt.service.VoucherService;

@RestController
@CrossOrigin
@RequestMapping("/api/")
public class VoucherController {
	
	@Autowired
	VoucherService voucherService;
	
	@PreAuthorize("hasAuthority('admin:create')")
	@PostMapping("/admin/voucher")
	public ResponseEntity<?> createVoucher(@RequestBody VoucherDto dto) {
		
		Voucher entity = new Voucher();

		entity = voucherService.createVoucher(dto);
		
		dto.setId(entity.getId());
		
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAuthority('admin:update')")
	@PutMapping("/admin/voucher")
	public ResponseEntity<?> updateVoucher(@RequestBody VoucherDto dto) {
		Voucher entity = new Voucher();
		entity = voucherService.updateVoucher(dto);
		
		dto.setId(entity.getId());
		
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}
	@GetMapping("/voucher/{id}/get")
	public ResponseEntity<?> getVouchers(@PathVariable("id") Long id){
		return new ResponseEntity<>(voucherService.getVoucherById(id), HttpStatus.OK);
	}

	@DeleteMapping("/admin/voucher/{id}")
	public ResponseEntity<?> deleteVoucher(@PathVariable("id") Long id){
		voucherService.deleteVoucherById(id);
		
		return new ResponseEntity<>("Voucher with ID: " + id + " was deleted", HttpStatus.OK);
	}
	@GetMapping("/vouchers")
	public ResponseEntity<?> getVouchers(){
		return new ResponseEntity<>(voucherService.getAllVouchers(), HttpStatus.OK);
	}
	
}
