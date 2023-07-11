package com.tadashop.nnt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "size")
public class Size extends AbstractEntity{
	
	@Column(name = "size", nullable = false, length = 100)
	private String size;
	
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
