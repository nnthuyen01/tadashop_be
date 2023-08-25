package com.tadashop.nnt.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SizeResp implements Serializable{
	
	private Long id;

	private Integer quantity;

	private String size;
	
	private Long productId;

	
}
