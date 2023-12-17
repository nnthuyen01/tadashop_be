package com.tadashop.nnt.dto;

import java.util.Date;

import lombok.Data;

@Data
public class VoucherResp {
	private Long id;
	private String code;
	private Integer priceOffPercent;
	private Long userId;
	private String username;
	private Integer status;
	private Date expirationTime;
}
