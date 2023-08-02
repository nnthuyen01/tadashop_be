package com.tadashop.nnt.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "_order")
public class Order extends AbstractEntity {

	@Column(name = "state", nullable = false)
	private String state;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "create_date")
	private LocalDateTime createTime;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_User_id")
	private User orderUser;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference
	@JoinColumn(name = "order_Detail_id")
	private OrderDetail orderdetail;
	
	@JsonIgnore
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<OrderItem> orderItems;
}
