package com.tadashop.nnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import com.tadashop.nnt.model.Payment;


@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long>{
	@Query("SELECT p FROM Payment as p WHERE p.name = :name ")
	Payment findByName(String name);
}
