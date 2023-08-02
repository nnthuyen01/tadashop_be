package com.tadashop.nnt.service;

import java.util.List;

import com.tadashop.nnt.dto.VoucherDto;
import com.tadashop.nnt.model.Voucher;

public interface VoucherService {
	List<Voucher> getAllVouchers();
	Voucher createVoucher(VoucherDto dto);
	Voucher updateVoucher(VoucherDto dto);
	Voucher getVoucherById(Long id);
	void deleteVoucherById(Long id);
}
