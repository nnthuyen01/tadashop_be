package com.tadashop.nnt.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.model.User;
import com.tadashop.nnt.repository.UserRepo;

@RestController
@RequestMapping("/api/")
public class DemoController {
	@Autowired
	UserRepo userRepo;
	
	@GetMapping("/user/demo-controller")
	public ResponseEntity<String> sayHello() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String id;
		if (principal instanceof User) {
		    id = ((User) principal).getId().toString();
		} else {
		    id = principal.toString();
		}
		User user = userRepo.getReferenceById(Long.valueOf(id));
		return ResponseEntity.ok("Welcome "+ user.getFirstname() +" to our website");
	}
	
	@GetMapping("/demo-controller")
	public ResponseEntity<String> sayHelloGuest() {

		return ResponseEntity.ok("Welcome to our website");
	}
}
