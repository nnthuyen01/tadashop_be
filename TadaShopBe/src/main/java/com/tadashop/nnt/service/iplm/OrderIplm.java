package com.tadashop.nnt.service.iplm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.dto.OrderReq;
import com.tadashop.nnt.model.Cart;
import com.tadashop.nnt.model.Order;
import com.tadashop.nnt.model.OrderDetail;
import com.tadashop.nnt.model.OrderItem;
import com.tadashop.nnt.model.Payment;
import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.model.Size;
import com.tadashop.nnt.model.User;
import com.tadashop.nnt.model.Voucher;
import com.tadashop.nnt.repository.CartRepo;
import com.tadashop.nnt.repository.OrderDetailRepo;
import com.tadashop.nnt.repository.OrderItemRepo;
import com.tadashop.nnt.repository.OrderRepo;
import com.tadashop.nnt.repository.PaymentRepo;
import com.tadashop.nnt.repository.ProductRepo;
import com.tadashop.nnt.repository.SizeRepo;
import com.tadashop.nnt.repository.UserRepo;
import com.tadashop.nnt.repository.VoucherRepo;
import com.tadashop.nnt.service.OrderService;
import com.tadashop.nnt.service.SizeService;
import com.tadashop.nnt.utils.Utils;
import com.tadashop.nnt.utils.constant.StateOrderConstant;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderIplm implements OrderService {
	@Autowired
	UserRepo userRepo;
	@Autowired
	CartRepo cartRepo;
	@Autowired
	VoucherRepo voucherRepo;
	@Autowired
	ProductRepo productRepo;
	@Autowired
	PaymentRepo paymentRepo;
	@Autowired
	OrderRepo orderRepo;
	@Autowired
	OrderItemRepo orderItemRepo;
	@Autowired
	SizeRepo sizeRepo;
	@Autowired
	OrderDetailRepo orderDetailRepo;
	@Autowired
	SizeService sizeService;
	private Double price = (double) 0;
	private int quantity = 0;
	public Order createOrder(OrderReq orderReq) {
		Long userId = Utils.getIdCurrentUser();
		User users = userRepo.getReferenceById(userId);

		Order order = new Order();
		LocalDateTime date = LocalDateTime.now();
		order.setCreateTime(date);
		order.setOrderUser(users);
		order.setState(StateOrderConstant.Pending);


		OrderDetail orderDetail = new OrderDetail();

		orderDetail.setDeliveryAddress(orderReq.getDeliveryAddress());
		orderDetail.setNote(orderReq.getNote());
		orderDetail.setReceiverName(orderReq.getDifferentReceiverName());
		orderDetail.setReceiverPhone(orderReq.getDifferentReceiverPhone());
		

		Voucher voucher = voucherRepo.findByCode(orderReq.getDiscountCode());
		if (voucher != null) {
			orderDetail.setDisscountCode(orderReq.getDiscountCode());
			orderDetail.setVoucher(voucher);
			orderDetail.setPriceOff(voucher.getPrice());
		} else {
			orderDetail.setPriceOff(Double.valueOf(0));
		}

//		Payment payment = paymentRepo.getReferenceById(orderReq.getId_payment());
		Payment payment = paymentRepo.findByName(orderReq.getPaymentMethod());
		orderDetail.setPayment(payment);

		List<OrderItem> orderItems = new ArrayList<>();
		List<Cart> cartList = cartRepo.findByCartID_UserId(userId);

		if (cartList.size() > 0) {
			cartList.forEach(cart -> {

				Size sizeProduct = sizeRepo.getReferenceById(cart.getCartID().getProductSizeId());
				Product product = sizeProduct.getProduct();
				quantity += cart.getQuantity();
				price += (cart.getQuantity() * product.getPrice());
				OrderItem orderItem = new OrderItem();
				orderItem.setItemName(product.getName());
				orderItem.setQuantity(cart.getQuantity());
				orderItem.setPrice(product.getPrice());
				orderItem.setSize(sizeProduct);
				orderItems.add(orderItem);
				cartRepo.delete(cart);
			});
		}
		
		orderDetail.setQuantity(quantity);
		orderItems.forEach(orderItem ->{
			orderItem.setOrder(order);
			orderItemRepo.save(orderItem);
		});
		
		Double totalPrice = price - orderDetail.getPriceOff();
		orderDetail.setTotalPrice(totalPrice);
		
		order.setOrderItems(orderItems);
		order.setOrderdetail(orderDetail);
		orderRepo.save(order);

		
		return order;

	}
}
