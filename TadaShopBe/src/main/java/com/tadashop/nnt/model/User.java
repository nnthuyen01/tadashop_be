package com.tadashop.nnt.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tadashop.nnt.utils.constant.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")

public class User extends AbstractEntity implements UserDetails {

	@Column(name = "first_name", nullable = false)
	private String firstname;
	
	@Column(name = "last_name", nullable = false)
	private String lastname;
	
	@Column(name = "email", nullable = false)
	private String email;
	
	@Column(name = "phone", nullable = false)
	private String phone;
	
	@Column(name = "username", nullable = false)
	private String username;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@Column(name = "avatar")
	private String avatar;
	
	@Column(name = "amount_paid")
	private Double amountPaid = 0.0; 
	
	@Column(name = "enable")
	private Boolean enable = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
	private List<Voucher> vouchers;	
	
//	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//	private List<Token> tokens;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return role.getAuthorities();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enable;
	}
}