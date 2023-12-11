package com.tadashop.nnt.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class MonthlyRevenueResp implements Serializable{


	private int date;

	private Double totalRevenue;
	

}
