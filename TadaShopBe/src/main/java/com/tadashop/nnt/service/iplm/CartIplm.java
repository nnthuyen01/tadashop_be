package com.tadashop.nnt.service.iplm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.dto.CartDto;
import com.tadashop.nnt.dto.CartResp;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Cart;
import com.tadashop.nnt.model.CartID;
import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.model.Size;
import com.tadashop.nnt.repository.CartRepo;
import com.tadashop.nnt.repository.SizeRepo;
import com.tadashop.nnt.service.CartService;
import com.tadashop.nnt.service.SizeService;
import com.tadashop.nnt.utils.Utils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CartIplm implements CartService {
	@Autowired
	CartRepo cartRepo;
	@Autowired
	SizeRepo sizeRepo;
	@Autowired
	SizeService sizeService;

	@Override
	public void save(Long sizeId, int quantity) {

		Long userId = Utils.getIdCurrentUser();
		CartDto cartDto = new CartDto(new CartID(sizeId, userId), quantity);
		
		// Kiểm tra nếu quantity lớn hơn quantity trong sizeId
	    if (isQuantityExceedsSizeQuantity(cartDto)) {
	        throw new AppException("Số lượng vượt quá số lượng sản phẩm.");
	    }
	    
		update(cartDto);
	}
	public boolean isQuantityExceedsSizeQuantity(CartDto cartDto) {
	    int newSizeQuantity = getSizeQuantityById(cartDto.getCartID().getProductSizeId());
	    return cartDto.getQuantity() > newSizeQuantity;
	}

	public int getSizeQuantityById(Long sizeId) {
	   
	    return sizeRepo.getReferenceById(sizeId).getQuantity();
	}

	public void update(CartDto cartDto) {
		Cart updatedCart = cartRepo.findCartByUserIdAndProductSizeId(cartDto.getCartID().getUserId(),
				cartDto.getCartID().getProductSizeId());

		if (updatedCart == null) {

			Cart newCart = new Cart();
			BeanUtils.copyProperties(cartDto, newCart);
			cartRepo.save(newCart);
		} else {
			updatedCart.setQuantity(updatedCart.getQuantity() + cartDto.getQuantity());
			 if (updatedCart.getQuantity() > sizeRepo.getReferenceById(cartDto.getCartID().getProductSizeId()).getQuantity()) {
			        throw new AppException("Số lượng vượt quá số lượng sản phẩm.");
			    }
			cartRepo.save(updatedCart);
		}
	}

	@Override
	public List<CartResp> view(Long userID) {
		List<Cart> cartList = cartRepo.findByCartID_UserId(userID);
		List<CartResp> cartResps = new ArrayList<>();
		if (cartList != null) {
			cartList.forEach(cart -> {
				CartResp cartResp = new CartResp();
				Size size = sizeService.getSizeById(cart.getCartID().getProductSizeId());

				cartResp.setQuantity(cart.getQuantity());
				Product product = size.getProduct();
				cartResp.setItem(new CartResp.Items(size, product.getName(),product.getId(), product.getImage(), product.getPriceAfterDiscount()));
				cartResps.add(cartResp);
			});
			return cartResps;
		} else {
			return null;
		}
	}

	@Modifying
	@Transactional
	@Override
	public Integer deleteCart(CartID cartID) {
		return cartRepo.deleteByCartID(cartID);
	}

	@Override
	public Cart updateCart(CartID cartID, int quantity) {
		Cart updatedCart = cartRepo.findById(cartID).orElse(null);
		if (updatedCart != null) {
			updatedCart.setQuantity(quantity);
			if(updatedCart.getQuantity() > sizeRepo.getReferenceById(cartID.getProductSizeId()).getQuantity())
				throw new AppException("Số lượng vượt quá số lượng sản phẩm.");
		}
		return updatedCart;
	}

	@Override
	public Cart decrease(CartID cartID) {
		Cart updatedCart = cartRepo.findById(cartID).orElse(null);
		if (updatedCart != null && updatedCart.getQuantity() - 1 > 0) {
			updatedCart.setQuantity(updatedCart.getQuantity() - 1);
		}
		return updatedCart;
	}

	@Override
	public Cart increase(CartID cartID) {
		Cart updatedCart = cartRepo.findById(cartID).orElse(null);
	  
		if (updatedCart != null && updatedCart.getQuantity() + 1 < 99) {
			updatedCart.setQuantity(updatedCart.getQuantity() + 1);			
		}
	    
		return updatedCart;
	}

	@Override
	public void deleteAllCartUser() {
		Long userId = Utils.getIdCurrentUser();
		try {
			cartRepo.deleteAllByCartID_UserId(userId);
		} catch (Exception e) {
			throw new AppException("Cart not found");
		}
	}
}
