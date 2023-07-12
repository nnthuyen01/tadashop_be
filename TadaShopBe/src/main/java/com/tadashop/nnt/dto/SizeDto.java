package com.tadashop.nnt.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SizeDto implements Serializable{
	
	private Long id;
	
	@NotEmpty(message = "Club name is required")
	private String size;
	
	private Long productId;

	
}
