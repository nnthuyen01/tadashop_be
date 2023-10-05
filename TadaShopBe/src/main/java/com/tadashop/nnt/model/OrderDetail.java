package com.tadashop.nnt.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_detail")
public class OrderDetail extends AbstractEntity{
	
	@Column(name ="delivery_address", nullable = false)
	private String deliveryAddress;
	
	@Column(name ="note")
	private String note;
	
	@Column(name ="receiver_phone")
	private String receiverPhone;
	
	@Column(name ="receiver_name")
	private String receiverName;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	
	@Column(name = "total_price",  nullable = false)
	private Double totalPrice;
	
	@Column(name ="disscount_code")
	private String disscountCode;
	
	@Column(name ="price_off")
	private Double priceOff;
	
	@OneToOne
    @JsonBackReference
    private Order order;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id")
	private Payment payment;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "voucher_id", nullable = true)
//	private Voucher voucher;
}
