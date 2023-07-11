package com.tadashop.nnt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "brand")
public class Brand extends AbstractEntity{
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "logo", length = 80)
	private String logo;

}
