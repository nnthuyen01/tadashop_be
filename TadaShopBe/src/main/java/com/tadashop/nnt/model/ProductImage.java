package com.tadashop.nnt.model;

import java.util.Objects;

import org.hibernate.Hibernate;

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
@Table( name = "product_image")
public class ProductImage extends AbstractEntity{
	
	@Column(name= "name", length = 100)
	private String name;

	@Column(name= "file_name", length = 100)
	private String fileName;
	
	@Column(name= "uri")
	private String uri;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || Hibernate.getClass(this) != Hibernate.getClass(obj))
			return false;
		ProductImage other = (ProductImage) obj;
		return getId() != null && Objects.equals(getId(), other.getId());
	}
	

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
