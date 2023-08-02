package com.tadashop.nnt.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Cart{

    @EmbeddedId
    private CartID cartID;

    private int quantity;
}