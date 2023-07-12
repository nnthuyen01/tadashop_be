package com.tadashop.nnt.service.iplm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tadashop.nnt.dto.ProductBriefDto;
import com.tadashop.nnt.dto.ProductDto;
import com.tadashop.nnt.dto.ProductImageDto;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Brand;
import com.tadashop.nnt.model.Club;
import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.model.ProductImage;
import com.tadashop.nnt.repository.ProductImageRepo;
import com.tadashop.nnt.repository.ProductRepo;
import com.tadashop.nnt.service.ProductService;
import com.tadashop.nnt.service.filestorage.FileStorageService;



@Service
public class ProductIplm implements ProductService{
	@Autowired
	private ProductRepo productRepository;
	
	@Autowired
	private ProductImageRepo productImageRepository;
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Transactional(rollbackFor = Exception.class )
	public ProductDto insertProduct(ProductDto dto) {

		Product entity = new Product();
		
		BeanUtils.copyProperties(dto, entity);
		
//		if (dto.getName() != null) {
//		    entity.setName(dto.getName());
//		} 
		var club = new Club();
		club.setId(dto.getClubId());
		entity.setClub(club);
		
		var brand = new Brand();
		brand.setId(dto.getBrandId());
		entity.setBrand(brand);

		if (dto.getImage() != null) {
			ProductImage img = new ProductImage();
			BeanUtils.copyProperties(dto.getImage(), img);
			var savedImg = productImageRepository.save(img);
			entity.setImage(savedImg);
		}
		
		if (dto.getImages() != null && dto.getImages().size() > 0) {
			var entityList = saveProductImages(dto);
			entity.setImages(entityList);
		}
		
		var savedProduct = productRepository.save(entity);
		dto.setId(savedProduct.getId());
		
		return dto;
	}
	
	@Transactional(rollbackFor = Exception.class )
	public ProductDto updateProduct(Long id, ProductDto dto) {
		var found = productRepository.findById(id).orElseThrow(()->new AppException("Product with id " + id + " Not Found"));
		
		//copy thanh phan nhung truong khong can copy
		String ignoreFields[] = new String[]{"createdDate", "image",  "images", "viewCount"};
		BeanUtils.copyProperties(dto,found ,ignoreFields);
		
		if(dto.getImage().getId() != null && found.getImage().getId()!=dto.getImage().getId()) {
			fileStorageService.deleteProductImageFile(found.getImage().getFileName());
			
			ProductImage img = new ProductImage();
			BeanUtils.copyProperties(dto.getImage(), img);
			
			productImageRepository.save(img);
			found.setImage(img);
			}
		
		var club = new Club();
		club.setId(dto.getClubId());
		found.setClub(club);
		
		var brand = new Brand();
		brand.setId(dto.getBrandId());
		found.setBrand(brand);
		
		if(dto.getImages().size() > 0) {
			var toDeleteFile = new ArrayList<ProductImage>();
			
			found.getImages().stream().forEach(item->{
				var existed = dto.getImages().stream().anyMatch(img->img.getId() == item.getId());
				
				if (!existed) toDeleteFile.add(item);
			});
			
			if(toDeleteFile.size() > 0) {
				toDeleteFile.stream().forEach(item -> {
					fileStorageService.deleteProductImageFile(item.getFileName());
					productImageRepository.delete(item);
				});
			}
			
			var imgList = dto.getImages().stream().map(item->{
				ProductImage img = new ProductImage();
				BeanUtils.copyProperties(item, img);
				return img;
			}).collect(Collectors.toSet());
			found.setImages(imgList);
		}
		
		var saveEntity = productRepository.save(found);
		dto.setId(saveEntity.getId());
		return dto;
	}
	
	public Page<ProductBriefDto> getProductBriefsByName(String name, Pageable pageable){
		var list = productRepository.findByNameContainsIgnoreCase(name, pageable);
		var newList = list.getContent().stream().map(item->{
			ProductBriefDto dto = new ProductBriefDto();
			BeanUtils.copyProperties(item, dto);
			
			dto.setClubName(item.getClub().getName());
			dto.setBrandName(item.getBrand().getName());
			dto.setImageFileName(item.getImage().getFileName());
			
			return dto;
		}).collect(Collectors.toList());
		
		var newPage = new PageImpl<ProductBriefDto>(newList, list.getPageable(), list.getTotalElements());
		
		return newPage;
	}
	
	private Set<ProductImage> saveProductImages(ProductDto dto){
		var entityList = new HashSet<ProductImage>();
		
		var newList = dto.getImages().stream().map(item->{
			ProductImage img = new ProductImage();
			BeanUtils.copyProperties(item, img);
			
			var savedImg = productImageRepository.save(img);
			item.setId(savedImg.getId());
			
			entityList.add(savedImg);
			return item;
		}).collect(Collectors.toList());
		
		dto.setImages(newList);
		return entityList;
				
	}
	
	@Transactional(rollbackFor = Exception.class )
	public void deleteProductById(Long id) {
		 var found = productRepository.findById(id)
				 .orElseThrow(()-> new AppException("Product Not Found"));
		 if (found.getImage() != null) {
			 fileStorageService.deleteProductImageFile(found.getImage().getFileName());
			 
			 productImageRepository.delete(found.getImage());
		 }
		 
		 if(found.getImages().size() > 0) {
			 found.getImages().stream().forEach(item->{
				 fileStorageService.deleteProductImageFile(item.getFileName());
				 productImageRepository.delete(item);
			 });
		 }
		 productRepository.delete(found);
	}

	public ProductDto getEditedProductById(Long id) {
		var found = productRepository.findById(id).orElseThrow(()->new AppException("Product with id " + id + " Not Found"));
		
		ProductDto dto = new ProductDto();
		BeanUtils.copyProperties(found, dto);
		
		dto.setClubId(found.getClub().getId());
		dto.setBrandId(found.getBrand().getId());
		
		var images = found.getImages().stream().map(item->{
			ProductImageDto imgDto = new ProductImageDto();
			BeanUtils.copyProperties(item, imgDto);
			return imgDto;
		}).collect(Collectors.toList());
		
		dto.setImages(images);
		ProductImageDto imageDto = new ProductImageDto();
		BeanUtils.copyProperties(found.getImage(), imageDto);
		dto.setImage(imageDto);
		
		return dto;
	}
	
	
	
	
}
