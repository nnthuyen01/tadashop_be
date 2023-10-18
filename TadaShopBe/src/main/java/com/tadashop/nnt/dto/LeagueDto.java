package com.tadashop.nnt.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LeagueDto implements Serializable {
	private Long id;
	@NotEmpty(message = "League name is required")
	private String name;

}
