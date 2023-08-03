package com.tadashop.nnt.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SizeDto implements Serializable{
	
	private Long id;
	
	@Min(value = 0)
	private Integer quantity;
	
	@NotEmpty(message = "Club name is required")
	private String size;
	
	private Long productId;

	
}
