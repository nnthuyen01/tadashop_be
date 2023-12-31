package com.tadashop.nnt.service.iplm;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.dto.ProductBriefDto;
import com.tadashop.nnt.dto.SizeDto;
import com.tadashop.nnt.dto.SizeResp;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Size;
import com.tadashop.nnt.model.Payment;
import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.repository.ProductRepo;
import com.tadashop.nnt.repository.SizeRepo;
import com.tadashop.nnt.service.SizeService;

@Service
public class SizeIplm implements SizeService {
	@Autowired
	private SizeRepo sizeRepo;
	@Autowired
	private ProductRepo productRepo;

	public Size saveSize(SizeDto dto) {
		boolean check = productRepo.existsById(dto.getProductId());
		if (!check)
			throw new AppException("not found product id " + dto.getProductId());

		var productCheck = productRepo.findById(dto.getProductId());

		List<?> foundedList = sizeRepo.findBySizeAndProduct(dto.getSize(), productCheck.get());
		if (foundedList.size() > 0) {
			System.out.println(foundedList.size());
			throw new AppException("Size name is existed");
		}

		Size entity = new Size();
		BeanUtils.copyProperties(dto, entity);

		var product = new Product();
		product.setId(dto.getProductId());
		entity.setProduct(product);

		var productTemp = productRepo.findById(product.getId());
		productTemp.get().setTotalQuantity(productTemp.get().getTotalQuantity() + dto.getQuantity());
		productRepo.save(productTemp.get());

		return sizeRepo.save(entity);
	}

	public SizeResp saveSize1(SizeDto dto) {
		boolean check = productRepo.existsById(dto.getProductId());
		if (!check)
			throw new AppException("not found product id " + dto.getProductId());

		var productCheck = productRepo.findById(dto.getProductId());

		List<?> foundedList = sizeRepo.findBySizeAndProduct(dto.getSize(), productCheck.get());
		if (foundedList.size() > 0) {
			System.out.println(foundedList.size());
			throw new AppException("Size name is existed");
		}

		Size entity = new Size();
		BeanUtils.copyProperties(dto, entity);

		var product = new Product();
		product.setId(dto.getProductId());
		entity.setProduct(product);

		var productTemp = productRepo.findById(product.getId());
		productTemp.get().setTotalQuantity(productTemp.get().getTotalQuantity() + dto.getQuantity());
		productRepo.save(productTemp.get());

		sizeRepo.save(entity);
		
		SizeResp resp = new SizeResp();
		BeanUtils.copyProperties(dto, resp);
		resp.setId(entity.getId());
		resp.setProductName(productTemp.get().getName());
		return resp;
		
	}

	public SizeDto updateSize(Long id, SizeDto dto) {
		var found = sizeRepo.findById(id).orElseThrow(() -> new AppException("Size with id " + id + " Not Found"));

		boolean check = productRepo.existsById(dto.getProductId());
		if (!check)
			throw new AppException("not found product id " + dto.getProductId());

		var productCheck = productRepo.findById(dto.getProductId());

		List<?> foundedList = sizeRepo.findBySizeAndProduct(dto.getSize(), productCheck.get());

		if (foundedList.size() >= 1 && !found.getSize().equals(dto.getSize())) {
			throw new AppException("Size name is existed");
		}

		if (dto.getSize() != null) {
			found.setSize(dto.getSize());
		}

		var product = new Product();
		product.setId(dto.getProductId());
		found.setProduct(product);

		var productTemp = productRepo.findById(product.getId());
		if (dto.getQuantity() > found.getQuantity()) {
			Integer temp = dto.getQuantity() - found.getQuantity();
			productTemp.get().setTotalQuantity(productTemp.get().getTotalQuantity() + temp);
		}
		if (dto.getQuantity() < found.getQuantity()) {
			Integer temp = found.getQuantity() - dto.getQuantity();
			productTemp.get().setTotalQuantity(productTemp.get().getTotalQuantity() - temp);
		}
		productRepo.save(productTemp.get());

		found.setQuantity(dto.getQuantity());

		var saveEntity = sizeRepo.save(found);
		dto.setId(saveEntity.getId());
		return dto;
	}
	
	public SizeResp updateSize1(Long id, SizeDto dto) {
		var found = sizeRepo.findById(id).orElseThrow(() -> new AppException("Size with id " + id + " Not Found"));

		boolean check = productRepo.existsById(dto.getProductId());
		if (!check)
			throw new AppException("not found product id " + dto.getProductId());

		var productCheck = productRepo.findById(dto.getProductId());

		List<?> foundedList = sizeRepo.findBySizeAndProduct(dto.getSize(), productCheck.get());

		if (foundedList.size() >= 1 && !found.getSize().equals(dto.getSize())) {
			throw new AppException("Size name is existed");
		}

		if (dto.getSize() != null) {
			found.setSize(dto.getSize());
		}

		var product = new Product();
		product.setId(dto.getProductId());
		found.setProduct(product);

		var productTemp = productRepo.findById(product.getId());
		if (dto.getQuantity() > found.getQuantity()) {
			Integer temp = dto.getQuantity() - found.getQuantity();
			productTemp.get().setTotalQuantity(productTemp.get().getTotalQuantity() + temp);
		}
		if (dto.getQuantity() < found.getQuantity()) {
			Integer temp = found.getQuantity() - dto.getQuantity();
			productTemp.get().setTotalQuantity(productTemp.get().getTotalQuantity() - temp);
		}
		productRepo.save(productTemp.get());

		found.setQuantity(dto.getQuantity());

		var saveEntity = sizeRepo.save(found);
		dto.setId(saveEntity.getId());
		
		SizeResp resp = new SizeResp();
		BeanUtils.copyProperties(dto, resp);
		resp.setId(saveEntity.getId());
		resp.setProductName(productTemp.get().getName());
		return resp;
	}

	public void deleteSizeById(Long id) {
		var check = sizeRepo.existsById(id);
		if (!check)
			throw new AppException("Size ID not found");

		try {
			var found = sizeRepo.findById(id);
			var productCheck = productRepo.findById(found.get().getProduct().getId());
			Integer temp = productCheck.get().getTotalQuantity() - found.get().getQuantity();
			productCheck.get().setTotalQuantity(temp);
			productRepo.save(productCheck.get());
			sizeRepo.deleteById(id);
		} catch (Exception e) {
			throw new AppException("Can't delete because have order for this product ID");
		}
	}

	public Size getSizeById(Long id) {
		var check = sizeRepo.findById(id);
		if (!check.isPresent()) {
			throw new AppException("Size ID not found");
		}
		Size size = check.get();
		return size;
	}

	@Override
	public List<SizeResp> findAll() {
		var list = sizeRepo.findAll();
		var newList = list.stream().map(item -> {
			SizeResp dto = new SizeResp();
			BeanUtils.copyProperties(item, dto);
			dto.setProductId(item.getProduct().getId());
			dto.setProductName(item.getProduct().getName());
			return dto;
		}).collect(Collectors.toList());
		return newList;
	}

	public List<Size> findAllSizeByProduct(Long id) {
		var check = productRepo.existsById(id);

		if (!check) {
			throw new AppException("Product ID not found");
		}
		var sizes = sizeRepo.findSizeByProductId(id);
		return sizes;

	}
	
	public Page<SizeResp> findAllByQuerySize(String name, Pageable pageable){
		var list = sizeRepo.findBySizeOrProductNameContainsIgnoreCase(name, pageable);
		var newList = list.stream().map(item -> {
			SizeResp dto = new SizeResp();
			BeanUtils.copyProperties(item, dto);
			dto.setProductId(item.getProduct().getId());
			dto.setProductName(item.getProduct().getName());
			return dto;
		}).collect(Collectors.toList());
		
		var newPage = new PageImpl<SizeResp>(newList, list.getPageable(), list.getTotalElements());

		return newPage;
	}
}
