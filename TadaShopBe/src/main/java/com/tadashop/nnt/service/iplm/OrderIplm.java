package com.tadashop.nnt.service.iplm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.dto.OrderDetailResp;
import com.tadashop.nnt.dto.OrderReq;
import com.tadashop.nnt.exception.AppException;
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

	@Transactional
	public Order createOrder(OrderReq orderReq) {
		Long userId = Utils.getIdCurrentUser();
		User users = userRepo.getReferenceById(userId);

		Order order = new Order();
		LocalDateTime date = LocalDateTime.now();
		order.setCreateTime(date);
		order.setOrderUser(users);
		order.setState(0);

		OrderDetail orderDetail = new OrderDetail();

		orderDetail.setDeliveryAddress(orderReq.getDeliveryAddress());
		orderDetail.setNote(orderReq.getNote());
		orderDetail.setReceiverName(orderReq.getDifferentReceiverName());
		orderDetail.setReceiverPhone(orderReq.getDifferentReceiverPhone());

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
				price += (cart.getQuantity() * product.getPriceAfterDiscount());
				OrderItem orderItem = new OrderItem();
				orderItem.setItemName(product.getName());
				orderItem.setQuantity(cart.getQuantity());
				orderItem.setPrice(product.getPriceAfterDiscount());
				orderItem.setSize(sizeProduct);
				orderItems.add(orderItem);
				cartRepo.delete(cart);
			});
		}

		orderDetail.setQuantity(quantity);
//		orderItems.forEach(orderItem ->{
//			orderItem.setOrder(order);
//			orderItemRepo.save(orderItem);
//		});

		Voucher voucher = voucherRepo.findByCode(orderReq.getDiscountCode());
		if (voucher != null) {
			orderDetail.setDisscountCode(orderReq.getDiscountCode());
			orderDetail.setPriceOff((price * voucher.getPriceOffPercent()) / 100);
		} else {
			orderDetail.setPriceOff(Double.valueOf(0));
		}

		Double totalPrice = price - orderDetail.getPriceOff();
		orderDetail.setTotalPrice(totalPrice);

		order.setOrderItems(orderItems);
		order.setOrderdetail(orderDetail);
		orderRepo.save(order);
		orderDetail.setOrder(order);
		orderItems.forEach(orderItem -> {
			orderItem.setOrder(order);
			orderItemRepo.save(orderItem);
		});

		return order;

	}

	public List<Order> getAllOrders() {
		return orderRepo.findAll();
	}

	public Page<Order> findAllOrder(Pageable pageable) {
		return orderRepo.findAll(pageable);
	}

	public Page<Order> searchOrder(final String kt, Pageable pageable) {
		return orderRepo.findByStateContaining(kt, pageable);
	}

	public List<Order> getAllOrdersByUser() {

		Long userId = Utils.getIdCurrentUser();

		List<Order> orders = orderRepo.findByUserId(userId);
		if (!orders.isEmpty()) {
			return orders;
		} else {
			throw new AppException("Order Not Found");
		}
	}
	
	public Page<Order> getOrderHistory(final Long userId, Pageable pageable) {
		if (userId == null) {
			throw new IllegalArgumentException("User id cannot be null");
		}
		User user = userRepo.getReferenceById(userId);
		if (user == null) {
			throw new AppException("User not found");
		}
		Page<Order> orders = orderRepo.findByOrderUser(user, pageable);
		if (!orders.isEmpty()) {
			return orders;
		} else {
			throw new AppException("Order Not Found");
		}
	}

	public OrderDetailResp findByIdOrder(Long id) {
		Order order = orderRepo.findById(id).orElseThrow(() -> {
			throw new AppException("not found order id" + id);
		});
		OrderDetailResp orderDetailResp = new OrderDetailResp();
		orderDetailResp.setOrder(order);
		orderDetailResp.setItems(new ArrayList<>());
		List<OrderItem> orderItems = order.getOrderItems();
		orderItems.forEach(orderItem -> {
			OrderDetailResp.Items items = new OrderDetailResp.Items();
			BeanUtils.copyProperties(orderItem, items);
//	            OrderDetailResp.Items items = modelMapper.map(orderItem,OrderDetailResp.Items.class);
			items.setProductSize(orderItem.getSize());
			items.setProduct(orderItem.getSize().getProduct());
			items.setTotalPrice(orderItem.getPrice() * orderItem.getQuantity());
			orderDetailResp.getItems().add(items);
		});
		return orderDetailResp;
	}

	public Long countOrderByDay(int day, int month, int year) {
		if (day == 0 && month == 0 && year == 0) {
			return orderRepo.count();
		}
		if (!GenericValidator.isDate(year + "-" + month + "-" + day, "yyyy-MM-dd", false))
			throw new AppException("Wrong day");

		LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0, 0);

		return orderRepo.countAllTimeGreaterThanEqual(date);

	}

	public Double countRevenueByDay(int day, int month, int year) {
		if (day == 0 && month == 0 && year == 0) {
			return orderRepo.totalAllRevenue();
		}
		if (!GenericValidator.isDate(year + "-" + month + "-" + day, "yyyy-MM-dd", false))
			throw new AppException("Wrong day");

		LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0, 0);

		return orderRepo.totalRevenue(date);
	}

	public List<Order> getOrderByStatus(int status) {

		StateOrderConstant state = StateOrderConstant.values()[status];
		List<Order> orders = orderRepo.findByState(state);
		if (!orders.isEmpty()) {
			return orders;
		} else {
			throw new AppException("Order Not Found");
		}
	}


	public Order updateStatusOrder(Long orderId, int status) {
		var check = orderRepo.findById(orderId);
		if (!check.isPresent()) {
			throw new AppException("Product Id not found");
		}
		Order orderUpdate = check.get();
		orderUpdate.setState(status);
		orderRepo.save(orderUpdate);
		return orderUpdate;
	}

}
