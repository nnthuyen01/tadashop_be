package com.tadashop.nnt.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.dto.BrandDto;
import com.tadashop.nnt.dto.VoucherDto;
import com.tadashop.nnt.model.Brand;
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
		
		
		return new ResponseEntity<>(voucherService.createVoucher1(dto), HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAuthority('admin:update')")
	@PutMapping("/admin/voucher")
	public ResponseEntity<?> updateVoucher(@RequestBody VoucherDto dto) {
//		Voucher entity = new Voucher();
//		entity = voucherService.updateVoucher(dto);
		
//		dto.setId(entity.getId());
		
		return new ResponseEntity<>(voucherService.updateVoucher1(dto), HttpStatus.CREATED);
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
	
	@GetMapping("/vouchersHaveUsername")
	public ResponseEntity<?> getVouchersHaveUsername(@RequestParam("query") String query,
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
		return new ResponseEntity<>(voucherService.getAllVouchersHaveUsername(query, pageable), HttpStatus.OK);
	}
	
	@GetMapping("/vouchers/user")
	public ResponseEntity<?> getVouchersByUser(){
		return new ResponseEntity<>(voucherService.getAllVouchersByUser(), HttpStatus.OK);
	}
	@GetMapping("/voucher/user/code")
	public ResponseEntity<?> getVoucherByCode(@RequestParam("discountCode") String code){
		return new ResponseEntity<>(voucherService.findByLikeCode(code), HttpStatus.OK);
	}
	@GetMapping("/vouchers/find")
	public ResponseEntity<?> getVouchersByCode(@RequestParam("query") String query, 
			@PageableDefault(size = 2, sort = "code", direction = Sort.Direction.ASC) Pageable pageable ) {
		Page<Voucher> list = voucherService.findByCode(query, pageable);
		
		List<VoucherDto> newList = list.getContent().stream().map(item -> {
			VoucherDto dto = new VoucherDto();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).collect(Collectors.toList());
		
		Page<VoucherDto> newPage =  new PageImpl<VoucherDto>(newList, pageable, list.getTotalElements());

		return new ResponseEntity<>(newPage, HttpStatus.OK);
	}
	
}
