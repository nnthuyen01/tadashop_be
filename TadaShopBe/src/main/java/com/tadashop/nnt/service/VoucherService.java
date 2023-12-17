package com.tadashop.nnt.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tadashop.nnt.dto.VoucherDto;
import com.tadashop.nnt.dto.VoucherResp;
import com.tadashop.nnt.model.Voucher;

public interface VoucherService {
	List<Voucher> getAllVouchers();
	List<Voucher> getAllVouchersByUser();
	Voucher createVoucher(VoucherDto dto);
	Voucher updateVoucher(VoucherDto dto);
	
	
	VoucherResp createVoucher1(VoucherDto dto);
	VoucherResp updateVoucher1(VoucherDto dto);
	
	Voucher getVoucherById(Long id);
	void deleteVoucherById(Long id);
	Page<Voucher> findByCode(String code, Pageable pageable);
	
	Voucher findByLikeCode(String code);
	
	Page<VoucherResp> getAllVouchersHaveUsername(String query, Pageable pageable);
}
