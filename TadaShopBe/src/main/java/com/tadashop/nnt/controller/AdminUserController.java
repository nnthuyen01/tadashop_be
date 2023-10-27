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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.dto.BrandDto;
import com.tadashop.nnt.dto.UserResp;
import com.tadashop.nnt.model.Brand;
import com.tadashop.nnt.model.User;
import com.tadashop.nnt.service.UserService;

@RestController
@RequestMapping("/api/")
public class AdminUserController {
	@Autowired
	private UserService userService;

	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/users")
	public ResponseEntity<?> getUsers() {
		List<?> list = userService.findAll();
		List<UserResp> newList = list.stream().map(item -> {
			UserResp dto = new UserResp();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).collect(Collectors.toList());

		return new ResponseEntity<>(newList, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/find/users")
	public ResponseEntity<?> getUsers(
			@PageableDefault(size = 2, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<?> list = userService.findAll(pageable);

		List<UserResp> newList = list.getContent().stream().map(item -> {
			UserResp dto = new UserResp();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).collect(Collectors.toList());

		Page<UserResp> newPage = new PageImpl<UserResp>(newList, pageable, list.getTotalElements());

		return new ResponseEntity<>(newPage, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('admin:update')")
	@PutMapping("/admin/users/disable/{userId}")
	public ResponseEntity<?> disable(@PathVariable Long userId) {
		userService.disableUserById(userId);
		return  new ResponseEntity<>("sucess", HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/query/users")
	public ResponseEntity<?> getUsersQuery(@RequestParam("query") String query, 
			@PageableDefault(size = 2, sort = "id", direction = Sort.Direction.ASC) Pageable pageable ) {
		Page<?> list = userService.findByUsername(query, pageable);
		
		List<UserResp> newList = list.stream().map(item -> {
			UserResp dto = new UserResp();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).collect(Collectors.toList());
		
		Page<UserResp> newPage =  new PageImpl<UserResp>(newList, pageable, list.getTotalElements());

		return new ResponseEntity<>(newPage, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/account/{userId}")
	public ResponseEntity<?> getUser(@PathVariable Long userId) {
		User user = userService.findUserById(userId);
		UserResp resp = new UserResp();
		BeanUtils.copyProperties(user, resp);
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
}
