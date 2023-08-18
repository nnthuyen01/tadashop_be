package com.tadashop.nnt.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "verification")
@AllArgsConstructor
@NoArgsConstructor
public class Verification extends AbstractEntity implements Serializable {
	private static final int EXPIRATION_TIME = 5;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	private Date expirationTime;

	private String token;

	public Verification(User user, String token) {
		super();
		this.token = token;
		this.user = user;
		this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
	}

	public void setToken(String token) {
		this.token = token;
		this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
	}

	private Date calculateExpirationDate(int expirationTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(Calendar.MINUTE, expirationTime);
		return new Date(calendar.getTime().getTime());
	}
}
