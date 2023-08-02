package com.tadashop.nnt.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.dto.CartResp;
import com.tadashop.nnt.model.Cart;
import com.tadashop.nnt.model.CartID;
import com.tadashop.nnt.service.CartService;
import com.tadashop.nnt.utils.Utils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "AUTHORIZATION")
public class CartController {

	private final CartService cartService;

	@GetMapping("/cart")
	private ResponseEntity<?> getCart() {
		Long userId = Utils.getIdCurrentUser();
		List<CartResp> cartList = cartService.view(userId);
		if (cartList != null) {
			return new ResponseEntity<>(cartList, HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Item!");
		}
	}

	@PostMapping("/cart/add")
	private ResponseEntity<?> addProduct(@RequestParam Long productSizeId, @RequestParam int quantity) {
		cartService.save(productSizeId, quantity);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/cart/remove")
	public ResponseEntity<?> removeCartItem(@RequestParam Long productSizeId) {

		CartID cartID = getCartId(productSizeId);

		Integer check = cartService.deleteCart(cartID);
		if (check >= 1) {
			return new ResponseEntity<>(check, HttpStatus.OK);
		} else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart Product ID = " + cartID.getProductSizeId()
					+ " with User ID = " + cartID.getUserId() + " not exits ");
	}

	@PutMapping("/cart/update")
	public ResponseEntity<?> updateQuantity(@RequestParam Long productSizeId, @RequestParam int quantity) {

		CartID cartID = getCartId(productSizeId);
		Cart updatedCart = cartService.updateCart(cartID, quantity);
		if (quantity < 1) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Quantity Must Greater Than 0");
		}
		if (quantity > 99) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Quantity Must Lower Than 100");
		} else {
			if (updatedCart != null)
				return new ResponseEntity<>(updatedCart, HttpStatus.OK);
			else
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart Product ID = "
						+ cartID.getProductSizeId() + " with User ID = " + cartID.getUserId() + " not exits ");
		}
	}

	@PutMapping("/cart/increase/")
	public ResponseEntity<?> increaseQuantity(@RequestParam Long productSizeId) {

		CartID cartID = getCartId(productSizeId);
		Cart updatedCart = cartService.increase(cartID);
		if (updatedCart != null)
			return new ResponseEntity<>(updatedCart, HttpStatus.OK);
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart Product ID = "
					+ cartID.getProductSizeId() + " with User ID = " + cartID.getUserId() + " not exits ");
	}

	@PutMapping("/cart/decrease/")
	public ResponseEntity<?> decreaseQuantity(@RequestParam Long productSizeId) {

		CartID cartID = getCartId(productSizeId);
		Cart updatedCart = cartService.decrease(cartID);
		if (updatedCart != null)
			return new ResponseEntity<>(updatedCart, HttpStatus.OK);
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart Product ID = "
					+ cartID.getProductSizeId() + " with User ID = " + cartID.getUserId() + " not exits ");
	}

	@DeleteMapping("/cart")
	public ResponseEntity<?> deleteAllCartUser() {
		cartService.deleteAllCartUser();
		return ResponseEntity.ok("Success");
	}

	public CartID getCartId(Long productSizeId) {
		Long id = Utils.getIdCurrentUser();
		CartID cartID = new CartID(productSizeId, id);
		return cartID;
	}
}
