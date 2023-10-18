package com.tadashop.nnt.dto;

import java.util.List;

import com.tadashop.nnt.model.Voucher;
import com.tadashop.nnt.utils.constant.Role;

import lombok.Data;

@Data
public class UserResp {
	private Long id;
	private String username;
	private String firstname;
	private String lastname;
	private String email;
	private String phone;
	private String avatar;
	private Double amountPaid;
	private Role role;
	private Boolean enable;
	private List<Voucher> vouchers;
}
