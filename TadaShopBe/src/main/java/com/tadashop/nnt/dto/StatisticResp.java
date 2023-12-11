package com.tadashop.nnt.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class StatisticResp implements Serializable{


	private Long quantityUser;

	private Double totalRevenue;
	
	private Long quantityProduct;
}
