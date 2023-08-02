package com.tadashop.nnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.OrderDetail;

@Repository
public interface OrderDetailRepo extends JpaRepository<OrderDetail, Long>{

}
