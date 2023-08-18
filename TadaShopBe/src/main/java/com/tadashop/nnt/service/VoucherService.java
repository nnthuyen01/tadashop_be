package com.tadashop.nnt.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tadashop.nnt.dto.VoucherDto;
import com.tadashop.nnt.model.Voucher;

public interface VoucherService {
	List<Voucher> getAllVouchers();
	Voucher createVoucher(VoucherDto dto);
	Voucher updateVoucher(VoucherDto dto);
	Voucher getVoucherById(Long id);
	void deleteVoucherById(Long id);
	Page<Voucher> findByCode(String code, Pageable pageable);
}
