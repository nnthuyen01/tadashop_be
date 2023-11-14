package com.tadashop.nnt.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.dto.OrderDetailResp;
import com.tadashop.nnt.dto.OrderReq;
import com.tadashop.nnt.model.Order;


public interface OrderService {
	Order createOrder(OrderReq orderReq);
		
	List<Order> getAllOrders();

	Page<Order> findAllOrder(Pageable pageable);
	
	Page<Order> searchOrder(final String state, Pageable pageable);

	List<Order> getAllOrdersByUser();

	Page<Order> getOrderHistory(final Long userId, Pageable pageable);
	
    OrderDetailResp findByIdOrder(Long idOrder);

    Long countOrderByDay(int day, int month, int year);

    Double countRevenueByDay(int day, int month, int year);
       	
	
	List<Order> getOrderByStatus(int status);
	
	Order updateStatusOrder(Long orderId, int status);
}
