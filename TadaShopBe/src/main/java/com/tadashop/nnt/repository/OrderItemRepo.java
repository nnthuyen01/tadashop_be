package com.tadashop.nnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.OrderItem;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long>{

}
