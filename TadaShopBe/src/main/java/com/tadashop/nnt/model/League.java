package com.tadashop.nnt.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
@Table(name = "league")
public class League extends AbstractEntity{
	
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "league",  cascade = CascadeType.ALL)
    private List<Club> clubs;
}
