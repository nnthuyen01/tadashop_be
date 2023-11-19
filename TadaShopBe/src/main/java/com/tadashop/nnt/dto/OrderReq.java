package com.tadashop.nnt.dto;


import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class OrderReq {

	@NotEmpty(message = "Delivery address not empty")
	private String deliveryAddress;
	private String note;
	private String differentReceiverName;
	private String differentReceiverPhone;
	private Integer totalQuantity;
	private Double totalPrice;
	private String discountCode;
	@NotEmpty(message = "Paymend method not empty")
	private String paymentMethod;

}
