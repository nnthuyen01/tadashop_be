package com.tadashop.nnt.model;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tadashop.nnt.utils.constant.ProductStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product")
public class Product extends AbstractEntity{
	
	@Column(name= "name", nullable = false)
	private String name;
	
	@Column(name = "price", nullable = false)
	private Double price;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "is_featured")
	private Boolean isFeatured;
	
	@Column(name = "discount")
	private Float discount;
	
	@Column(name = "brief", length = 200, nullable = false)
	private String brief;
	
	@Column(name = "description", length = 2000, nullable = false)
	private String description;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "create_date")
	private Date createDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "update_date")
	private Date updateDate;
	
	@Column(name ="status")
	private ProductStatus status;
	
	@JsonIgnore
	@OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
	private List<Size> sizes;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "club_id")
	private Club club;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "brand_id")
	private Brand brand;
	
	@ManyToMany
	@JoinTable(name = "product_product_images",
			joinColumns = @JoinColumn(name = "product_id"),
			inverseJoinColumns = @JoinColumn(name = "product_images_id"))
	private Set<ProductImage> images = new LinkedHashSet<>();
	
	@OneToOne(orphanRemoval = true)
	@JoinColumn(name = "product_image_id")
	private ProductImage image;
	
	
	//Khoi tao gia tri date cho createDate truoc khi khoi tao entity truoc khi luu vao co so du lieu
	@PrePersist
	public void prePersist() {
		createDate = new Date();
		
		if( isFeatured == null) isFeatured = false;
	}
	
	//Khoi tao gia tri date updateDate sau khi cap nhat entity truoc khi luu vao co so du lieu
	@PreUpdate
	public void preUpdate() {
		updateDate = new Date();
	}
	
}
