package com.tadashop.nnt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.dto.OrderReq;
import com.tadashop.nnt.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class OrderController {
	@Autowired
	private final OrderService orderService;

	@PostMapping("/order")
	private ResponseEntity<?> create(@RequestBody OrderReq orderReq) {

		return new ResponseEntity<>(orderService.createOrder(orderReq), HttpStatus.OK);
	}
}