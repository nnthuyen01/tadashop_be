package com.tadashop.nnt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.model.Payment;
import com.tadashop.nnt.service.PaymentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "AUTHORIZATION")
public class PaymentController {

	private final PaymentService paymentService;

	// Get All payment
	@GetMapping("/payment")
	public ResponseEntity<?> findAll() {
		return new ResponseEntity<>(paymentService.findAll(), HttpStatus.OK);
	}

	// Get payment by ID
	@GetMapping("/payment/{paymentId}")
	public ResponseEntity<?> findById(@PathVariable Long paymentId) {
		Payment payment = paymentService.findById(paymentId);
		if (payment != null)
			return new ResponseEntity<>(payment, HttpStatus.OK);
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment ID not exits");
	}

	// Add payment
	@PreAuthorize("hasAuthority('admin:create')")
	@PostMapping("/admin/payment")
	public ResponseEntity<?> savePayment(@RequestBody Payment payment) {
		Payment paymentSave = paymentService.save(payment);
		return new ResponseEntity<>(paymentSave, HttpStatus.CREATED);
	}

	// Update Payment
	@PreAuthorize("hasAuthority('admin:update')")
	@PutMapping("/admin/payment")
	public ResponseEntity<?> updatePayment(@RequestBody Payment payment) {
		Payment paymentUpdate = paymentService.updatePayment(payment);
		if (paymentUpdate != null)
			return new ResponseEntity<>(paymentUpdate, HttpStatus.OK);
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment ID not exits");
	}

	// Delete Payment
	@PreAuthorize("hasAuthority('admin:delete')")
	@DeleteMapping("/admin/payment/{id}")
	public ResponseEntity<?> deletePayment(@PathVariable Long id) {
		boolean check = paymentService.deletePayment(id);
		if (check) {
			return new ResponseEntity<>(check, HttpStatus.OK);
		} else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment ID not exits");
	}
}