package com.tadashop.nnt.dto;

import com.tadashop.nnt.utils.constant.Role;

import lombok.Data;

@Data
public class UserResp {
	private Long id;
	private String firstname;
	private String lastname;
	private String email;
	private String phone;
	private String avatar;
	private Role role;
	private Boolean enable;
}
