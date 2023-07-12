package com.tadashop.nnt.service.iplm;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.dto.SizeDto;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Size;
import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.repository.ProductRepo;
import com.tadashop.nnt.repository.SizeRepo;
import com.tadashop.nnt.service.SizeService;


@Service
public class SizeIplm implements SizeService{
	@Autowired
	private SizeRepo sizeRepo;
	@Autowired
	private ProductRepo productRepo;

    public Size saveSize(SizeDto dto) {
    	boolean check = productRepo.existsById(dto.getProductId());
    	if (!check) throw new AppException("not found product id " + dto.getProductId());
    	
    	Size entity = new Size();		
		BeanUtils.copyProperties(dto, entity);
		
		var product = new Product();
		product.setId(dto.getProductId());
		entity.setProduct(product);
		
		return sizeRepo.save(entity);
    }

    public SizeDto updateSize(Long id, SizeDto dto) {
    	var found = sizeRepo.findById(id).orElseThrow(()->new AppException("Size with id " + id + " Not Found"));
    	
    	if(dto.getSize()!=null) {
    	found.setSize(dto.getSize());
    	}
    	
    	var product = new Product();
		product.setId(dto.getProductId());
		found.setProduct(product);
		
		var saveEntity = sizeRepo.save(found);
		dto.setId(saveEntity.getId());
		return dto;
    }

    public void deleteSizeById(Long id) {
        var check = sizeRepo.existsById(id);
        if (!check)
            throw new AppException("Size ID not found");

        try {
            sizeRepo.deleteById(id);
        }catch (Exception e){
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
    
    public List<Size> findAllSizeByProduct(Long id){
    	var check = productRepo.existsById(id);
    	
    	if(!check) {
    		throw new AppException("Product ID not found");
    	}
    	var sizes = sizeRepo.findSizeByProductId(id);
    	return sizes;

    }
}
