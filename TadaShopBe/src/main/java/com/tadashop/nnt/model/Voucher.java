package com.tadashop.nnt.model;

import java.util.Calendar;
import java.util.Date;

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
@Table(name = "voucher")
public class Voucher extends AbstractEntity {
	private static final int EXPIRATION_TIME = 31;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "priceOffPercent", nullable = false)
	private Integer priceOffPercent;

	@Column(name= "status",nullable = false)
	private Integer status;

	private Date expirationTime;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = true)
	private User user;

	public Voucher(User user, String code, Integer priceOffPercent,Integer status) {
		super();
		this.code = code;
		this.user = user;
		this.priceOffPercent = priceOffPercent;
		this.status = status;
		this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
	}

	public void setVoucher(String code, Integer priceOffPercent,Integer status) {
		this.code = code;
		this.priceOffPercent = priceOffPercent;
		this.status = status;
		this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
	}
	
	public void setVoucher(String code) {
		this.code = code;
		this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
	}

	private Date calculateExpirationDate(int expirationTime) {
		Calendar calendar = Calendar.getInstance();
//		calendar.setTimeInMillis(new Date().getTime());
//		calendar.add(Calendar.MINUTE, expirationTime);
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, expirationTime);
		return new Date(calendar.getTime().getTime());
	}
}
