package com.tadashop.nnt.dto;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class BrandDto implements Serializable{
	private Long id;
	private String name;
	private String logo;
	
	@JsonIgnore
	private MultipartFile logoFile;
}
