package com.tadashop.nnt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.Voucher;

@Repository
public interface VoucherRepo extends JpaRepository<Voucher, Long>{
	@Query("SELECT v FROM Voucher as v WHERE v.code = :code ")
	Voucher findByCode(String code);
	
	@Query("SELECT v FROM Voucher as v WHERE v.code = :code ")
	List<Voucher> findVouchersByCode(String code);
	void deleteVoucherByCode(String code);
	
	Page<Voucher> findByCodeContainsIgnoreCase(String code, Pageable pageable);
}
