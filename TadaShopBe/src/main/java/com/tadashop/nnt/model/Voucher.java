package com.tadashop.nnt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "voucher")
public class Voucher extends AbstractEntity{
	@Column(name= "code")
	private String name;
	
	@Column(name= "price")
	private Double price;
	
	@Column(name= "status",nullable = false)
	private Integer status;
}
