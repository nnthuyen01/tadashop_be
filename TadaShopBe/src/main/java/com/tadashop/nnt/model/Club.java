package com.tadashop.nnt.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "club")
public class Club extends AbstractEntity{
	
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	@JsonIgnore
	@OneToMany(mappedBy = "club",  cascade = CascadeType.ALL)
    private List<Product> products;
}
