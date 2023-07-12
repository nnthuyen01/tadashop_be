package com.tadashop.nnt.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductImageDto implements Serializable {
	private Long id;
	private String name;
	private String fileName;

	//Tương thích với upload của Antd
	private String uid; //de tuong thich voi upload component cua antd
	private String url;	//de tuong thich voi upload component cua antd
	private String status;
	private String response = "{\"status\": \"success\"}";
	
}
