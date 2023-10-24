package com.tadashop.nnt.service.iplm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tadashop.nnt.dto.ProductBriefDto;
import com.tadashop.nnt.dto.ProductDetailResp;
import com.tadashop.nnt.dto.ProductDto;
import com.tadashop.nnt.dto.ProductImageDto;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Brand;
import com.tadashop.nnt.model.Club;
import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.model.ProductImage;
import com.tadashop.nnt.model.Size;
import com.tadashop.nnt.repository.ProductImageRepo;
import com.tadashop.nnt.repository.ProductRepo;
import com.tadashop.nnt.repository.SizeRepo;
import com.tadashop.nnt.service.ProductService;
import com.tadashop.nnt.service.filestorage.FileStorageService;




@Service
public class ProductIplm implements ProductService{
	@Autowired
	private ProductRepo productRepository;
	
	@Autowired
	private ProductImageRepo productImageRepository;
	
	@Autowired
	private SizeRepo sizeRepo;
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Transactional(rollbackFor = Exception.class )
	public ProductDto insertProduct(ProductDto dto) {

		Product entity = new Product();
			
		dto.setTotalQuantity(entity.getTotalQuantity());
		
		BeanUtils.copyProperties(dto, entity);
		
		Double priceOff = Math.ceil((dto.getOriginalPrice() * (100 - dto.getDiscount())) / 100 / 1000) * 1000; // Làm tròn lên đến hàng ngàn
		entity.setPriceAfterDiscount(priceOff);
		
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
		dto.setPriceAfterDiscount(savedProduct.getPriceAfterDiscount());
		
		return dto;
	}
	
	@Transactional(rollbackFor = Exception.class )
	public ProductDto updateProduct(Long id, ProductDto dto) {
		var found = productRepository.findById(id).orElseThrow(()->new AppException("Product with id " + id + " Not Found"));
		
		//copy thanh phan nhung truong khong can copy
		String ignoreFields[] = new String[]{"createdDate", "image",  "images", "viewCount"};
		BeanUtils.copyProperties(dto,found ,ignoreFields);
		
		Double priceOff = Math.ceil((dto.getOriginalPrice() * (100 - dto.getDiscount())) / 100 / 1000) * 1000; // Làm tròn lên đến hàng ngàn
		found.setPriceAfterDiscount(priceOff);
		
		ProductImage imgUpdate = new ProductImage();
		BeanUtils.copyProperties(dto.getImage(), imgUpdate);
		productImageRepository.save(imgUpdate);
		System.out.print("INEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE" + dto.getImage().getId());
//		if(dto.getImage().getId() != null && found.getImage().getId()!=dto.getImage().getId()) {
			if(imgUpdate.getId() != null && found.getImage().getId()!=imgUpdate.getId()) {
			fileStorageService.deleteProductImageFile(found.getImage().getFileName());
			
			ProductImage img = new ProductImage();
			BeanUtils.copyProperties(imgUpdate, img);
			
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
			
//			// Xóa tất cả các hình ảnh cũ
//		    found.getImages().clear();
		    // Lưu và thêm các hình ảnh mới
		    Set<ProductImage> newImages = saveProductImages(dto); // Sử dụng phương thức saveProductImages bạn đã cung cấp
		    found.getImages().addAll(newImages);
			
			
			var imgList = dto.getImages().stream().map(item->{
				ProductImage img = new ProductImage();
				BeanUtils.copyProperties(item, img);
				return img;
			}).collect(Collectors.toSet());
			found.setImages(imgList);
		}
		
		var saveEntity = productRepository.save(found);
		dto.setId(saveEntity.getId());
		dto.setPriceAfterDiscount(saveEntity.getPriceAfterDiscount());
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
	
	public List<ProductBriefDto> getProducts(){
		var list = productRepository.findAll();
		var newList = list.stream().map(item->{
			ProductBriefDto dto = new ProductBriefDto();
			BeanUtils.copyProperties(item, dto);
			
			dto.setClubName(item.getClub().getName());
			dto.setBrandName(item.getBrand().getName());
			dto.setImageFileName(item.getImage().getFileName());
			
			return dto;
		}).collect(Collectors.toList());
		

		
		return newList;
	}
	
	private Set<ProductImage> saveProductImages(ProductDto dto){
		var entityList = new HashSet<ProductImage>();		
		
		var newList = dto.getImages().stream().map(item -> {
	        // Kiểm tra xem hình ảnh đã tồn tại trong danh sách entityList chưa
	        boolean isExisting = entityList.stream()
	                .anyMatch(existingImage -> existingImage.getId() != null && existingImage.getId().equals(item.getId()));

	        if (!isExisting) {
	            ProductImage img = new ProductImage();
	            BeanUtils.copyProperties(item, img);

	            var savedImg = productImageRepository.save(img);
	            item.setId(savedImg.getId());

	            entityList.add(savedImg);
	            return item;
	        }
	        
	        return null; // Trả về null cho các hình ảnh đã tồn tại
	    }).filter(Objects::nonNull) // Lọc ra các hình ảnh mới (không null)
	      .collect(Collectors.toList());
		
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

	public ProductDetailResp findProductById(Long id) {
		var found = productRepository.findById(id).orElseThrow(()->new AppException("Product with id " + id + " Not Found"));
		
		ProductDetailResp productDetailResp =new ProductDetailResp(); 
		
		List<Size> sizes = sizeRepo.findAllByProduct(found);
		
//		Integer tQuantity = 0;
//		for (Size size : sizes ) {
//			tQuantity += size.getQuantity();
//		}
//		productDetailResp.setTotalQuantity(tQuantity);
		System.out.println(found.getTotalQuantity());
		productDetailResp.setTotalQuantity(found.getTotalQuantity());
		System.out.println(productDetailResp.getTotalQuantity());
		
		BeanUtils.copyProperties(found, productDetailResp);
		var images = found.getImages().stream().map(item->{
			ProductImageDto imgDto = new ProductImageDto();
			BeanUtils.copyProperties(item, imgDto);
			return imgDto;
		}).collect(Collectors.toList());
		productDetailResp.setImages(images);
		
		ProductImageDto imageDto = new ProductImageDto();
		BeanUtils.copyProperties(found.getImage(), imageDto);
		productDetailResp.setImage(imageDto);
		productDetailResp.setSizes(sizes);
		
		return productDetailResp;
	}
	
	
}
