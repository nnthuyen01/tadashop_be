package com.tadashop.nnt.service;

import com.tadashop.nnt.dto.OrderReq;
import com.tadashop.nnt.model.Order;

public interface OrderService {
	Order createOrder(OrderReq orderReq);
}
