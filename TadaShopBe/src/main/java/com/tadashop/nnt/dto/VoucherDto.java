package com.tadashop.nnt.dto;

import lombok.Data;

@Data
public class VoucherDto {
	private Long id;
//	private String code;
	private Integer priceOffPercent;
	private Long userId;
	private Integer status;
}
