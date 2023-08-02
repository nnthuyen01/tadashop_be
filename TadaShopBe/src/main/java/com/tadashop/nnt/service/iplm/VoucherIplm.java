package com.tadashop.nnt.service.iplm;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.dto.VoucherDto;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Club;
import com.tadashop.nnt.model.Voucher;
import com.tadashop.nnt.repository.VoucherRepo;
import com.tadashop.nnt.service.VoucherService;

@Service
public class VoucherIplm implements VoucherService{
	@Autowired
	VoucherRepo voucherRepo;
	
	public Voucher createVoucher(VoucherDto dto) {
		List<Voucher> checkList = voucherRepo.findVouchersByCode(dto.getCode());
		if (checkList.size() > 0) {
			throw new AppException("Voucher with this code already used" );
		}
		Voucher voucher = new Voucher();
		BeanUtils.copyProperties(dto, voucher);
		return voucherRepo.save(voucher);
	}
	
	public Voucher updateVoucher(VoucherDto dto) {
		Optional<Voucher> existed = voucherRepo.findById(dto.getId());

		if (existed.isEmpty()) {
			throw new AppException("Voucher id " + dto.getId() + " does not exist");
		}

		try {
			Voucher existedVoucher = existed.get();
			existedVoucher.setCode(dto.getCode());
			existedVoucher.setPrice(dto.getPrice());
			existedVoucher.setStatus(dto.getStatus());
			return voucherRepo.save(existedVoucher);
		} catch (Exception ex) {
			throw new AppException("Voucher is updated fail");
		}
	}
	public Voucher getVoucherById(Long id) {
		Optional<Voucher> found =  voucherRepo.findById(id);
		
		if(found.isEmpty()) {
			throw new AppException("Voucher with id " + id + " does not exist");
		}
		
		return found.get();
	}
	public void deleteVoucherById(Long id) {
		Voucher existed = getVoucherById(id);
		
		voucherRepo.delete(existed);
	}
	public List<Voucher> getAllVouchers(){
		return voucherRepo.findAll();
	}
}
