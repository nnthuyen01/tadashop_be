package com.tadashop.nnt.service.iplm;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tadashop.nnt.dto.BrandDto;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Brand;
import com.tadashop.nnt.repository.BrandRepo;
import com.tadashop.nnt.service.BrandService;
import com.tadashop.nnt.service.filestorage.FileStorageService;

@Service
public class BrandIplm implements BrandService{
	@Autowired
	private BrandRepo brandRepository;
	
	@Autowired
	private FileStorageService fileStorageService;
	
	public Brand insertBrand(BrandDto dto) {
		List<?> foundedList = brandRepository.findByNameContainsIgnoreCase(dto.getName());
		
		if (foundedList.size() > 0) {
			throw new AppException("Brand name is existed");
		}
		
		Brand entity = new Brand();
		
		BeanUtils.copyProperties(dto, entity);
		
		if(dto.getLogoFile() != null) {
			String filename = fileStorageService.storeLogoFile(dto.getLogoFile());
			
			entity.setLogo(filename);
			dto.setLogoFile(null);
		}
		
		return brandRepository.save(entity);
	}
	public List<?> findAll(){
		return brandRepository.findAll();
		
	}
	
	public Page<Brand> findAll(Pageable pageable) {
		return brandRepository.findAll(pageable);
	}
	
	public Page<Brand> findByName(String name,Pageable pageable) {
		return brandRepository.findByNameContainsIgnoreCase(name, pageable);
	}
	public Brand findById(Long id) {

		Optional<Brand> found= brandRepository.findById(id);
		
		if(found.isEmpty()) {
			throw new AppException("Brand with id " + id + " does not existed");
		}
		
		return found.get();
	}
	
	public void deleteId(Long id) {

		Brand existed = findById(id);
		
		brandRepository.delete(existed);
	}
	
	public Brand updateBrand(Long id, BrandDto dto) {
		Optional<Brand> found = brandRepository.findById(id);
		
		if (found.isEmpty()) {
			throw new AppException("Brand not found");
		}
		
		Brand entity = new Brand();
		Brand entity1 = found.get();
		
		BeanUtils.copyProperties(dto, entity);
		
		if(dto.getName()==null || dto.getName().isBlank()) {
			entity.setName(entity1.getName());
		}
		if(dto.getLogoFile() == null) {
			entity.setLogo(entity1.getLogo());
		}
				
		if(dto.getLogoFile() != null) {
			String filename = fileStorageService.storeLogoFile(dto.getLogoFile());
			
			entity.setLogo(filename);
			dto.setLogoFile(null);
		}

		return brandRepository.save(entity);
	}
}
