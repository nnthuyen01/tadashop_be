package com.tadashop.nnt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "order_item")
public class OrderItem extends AbstractEntity {
	@Column(name = "item_name", nullable = false)
	private String itemName;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "price", nullable = false)
	private Double price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "size_id")
	private Size size;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

}
