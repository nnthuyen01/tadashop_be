package com.tadashop.nnt.service.iplm;

import java.io.Console;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.dto.SizeResp;
import com.tadashop.nnt.dto.VoucherDto;
import com.tadashop.nnt.dto.VoucherResp;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Club;
import com.tadashop.nnt.model.User;
import com.tadashop.nnt.model.Voucher;
import com.tadashop.nnt.repository.UserRepo;
import com.tadashop.nnt.repository.VoucherRepo;
import com.tadashop.nnt.service.VoucherService;
import com.tadashop.nnt.utils.Utils;

@Service
public class VoucherIplm implements VoucherService {
	@Autowired
	VoucherRepo voucherRepo;
	@Autowired
	UserRepo userRepo;

	public Voucher createVoucher(VoucherDto dto) {
		Voucher voucher = new Voucher();
		BeanUtils.copyProperties(dto, voucher);

		Optional<User> userOptional = userRepo.findById(dto.getUserId());
		if (!userOptional.isPresent()) {
			throw new AppException("User with id " + dto.getUserId() + " does not exist");
		}

		User user = userOptional.get();

		voucher.setUser(user);

		String randomPart = RandomStringUtils.randomAlphanumeric(4).toUpperCase();
		String code = "TADA" + randomPart;
		voucher.setVoucher(code);

		voucher = voucherRepo.save(voucher);

		userRepo.save(user);
		return voucher;
	}	

	public Voucher updateVoucher(VoucherDto dto) {
		Optional<Voucher> existed = voucherRepo.findById(dto.getId());

		if (existed.isEmpty()) {
			throw new AppException("Voucher id " + dto.getId() + " does not exist");
		}

		try {
			Voucher existedVoucher = existed.get();
			existedVoucher.setPriceOffPercent(dto.getPriceOffPercent());
			existedVoucher.setStatus(dto.getStatus());
			return voucherRepo.save(existedVoucher);
		} catch (Exception ex) {
			throw new AppException("Voucher is updated fail");
		}
	}

	public VoucherResp createVoucher1(VoucherDto dto) {
		Voucher voucher = new Voucher();
		BeanUtils.copyProperties(dto, voucher);

		Optional<User> userOptional = userRepo.findById(dto.getUserId());
		if (!userOptional.isPresent()) {
			throw new AppException("User with id " + dto.getUserId() + " does not exist");
		}

		User user = userOptional.get();

		voucher.setUser(user);

		String randomPart = RandomStringUtils.randomAlphanumeric(4).toUpperCase();
		String code = "TADA" + randomPart;
		voucher.setVoucher(code);

		voucher = voucherRepo.save(voucher);

		userRepo.save(user);
		VoucherResp resp = new VoucherResp();
		BeanUtils.copyProperties(voucher, resp);
		resp.setUserId(voucher.getUser().getId());
		resp.setUsername(voucher.getUser().getUsername());
		return resp;
	}	

	public VoucherResp updateVoucher1(VoucherDto dto) {
		Optional<Voucher> existed = voucherRepo.findById(dto.getId());

		if (existed.isEmpty()) {
			throw new AppException("Voucher id " + dto.getId() + " does not exist");
		}

		try {
			Voucher existedVoucher = existed.get();
			existedVoucher.setPriceOffPercent(dto.getPriceOffPercent());
			existedVoucher.setStatus(dto.getStatus());
			voucherRepo.save(existedVoucher);
			VoucherResp resp = new VoucherResp();
			BeanUtils.copyProperties(existedVoucher, resp);
			resp.setUserId(existedVoucher.getUser().getId());
			resp.setUsername(existedVoucher.getUser().getUsername());
			return resp;
		} catch (Exception ex) {
			throw new AppException("Voucher is updated fail");
		}
	}
	
	public Voucher getVoucherById(Long id) {
		Optional<Voucher> found = voucherRepo.findById(id);

		if (found.isEmpty()) {
			throw new AppException("Voucher with id " + id + " does not exist");
		}

		return found.get();
	}

	public void deleteVoucherById(Long id) {
		Voucher existed = getVoucherById(id);

		voucherRepo.delete(existed);
	}

	public List<Voucher> getAllVouchers() {
		return voucherRepo.findAll();
	}

	public Page<VoucherResp> getAllVouchersHaveUsername(String query, Pageable pageable){
		var list = voucherRepo.findByCodeOrUsernameContainsIgnoreCase(query, pageable);
		var newList = list.stream().map(item -> {
			VoucherResp resp = new VoucherResp();
			BeanUtils.copyProperties(item, resp);		
			resp.setUserId(item.getUser().getId());
			resp.setUsername(item.getUser().getUsername());
			return resp;
		}).collect(Collectors.toList());
		
		var newPage = new PageImpl<VoucherResp>(newList, list.getPageable(), list.getTotalElements());

		return newPage;
	}
	
	public List<Voucher> getAllVouchersByUser() {
		Long userId = Utils.getIdCurrentUser();

		var list = voucherRepo.findByIdUserNativeQuery(userId);
		return list;
	}

	public Page<Voucher> findByCode(String code, Pageable pageable) {
		return voucherRepo.findByCodeContainsIgnoreCase(code, pageable);
	}

	public Voucher findByLikeCode(String code) {
		Long userId = Utils.getIdCurrentUser();
		Voucher voucher = voucherRepo.findByCodeAndUserIdOrAdminId(code, userId);
		
		if (voucher != null && voucher.getStatus() == 1) {
			return voucher;
		} else {
			throw new AppException("false");
		}
	}
}
