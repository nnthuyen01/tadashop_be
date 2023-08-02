package com.tadashop.nnt.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	
	@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
	
    @JsonIgnore
    @OneToMany(mappedBy = "size",fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
}
