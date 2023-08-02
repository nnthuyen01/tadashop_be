package com.tadashop.nnt.dto;

import lombok.Data;

@Data
public class VoucherDto {
	private Long id;
	private String code;
	private Double price;
	private Integer status;
}
