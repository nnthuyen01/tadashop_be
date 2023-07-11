package com.tadashop.nnt.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.dto.BrandDto;
import com.tadashop.nnt.exception.FileStorageException;
import com.tadashop.nnt.model.Brand;
import com.tadashop.nnt.repository.BrandRepo;
import com.tadashop.nnt.service.BrandService;
import com.tadashop.nnt.service.filestorage.FileStorageService;
import com.tadashop.nnt.service.iplm.MapValidationErrorService;

import org.springframework.web.bind.annotation.PostMapping;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/")

public class BrandController {
	@Autowired
	private BrandService brandService;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private BrandRepo brandRepository;
	
	@Autowired
	MapValidationErrorService mapValidationErrorService;

	// consumes là các kiểu dữ liệu được sử dụng cho phương thức.
	@PostMapping(value = "/admin/brand", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> createBrand(@Valid @ModelAttribute BrandDto dto, BindingResult result) {

		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);

		if (responseEntity != null) {
			return responseEntity;
		}
		Brand entity = brandService.insertBrand(dto);

		dto.setId(entity.getId());
		dto.setLogo(entity.getLogo());

		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}

//	Gửi thông tin tên file của hình ảnh, và sử dụng tên file này để đọc các thông tin được upload trước đó dưới server
	@GetMapping("/brand/logo/{filename:.+}")
	public ResponseEntity<?> downloadFile(@PathVariable String filename, HttpServletRequest request) {
		// Trả về đối tượng resource, và sử dụng resource để xác định kiểu nội dụng cho
		// file
		Resource resource = fileStorageService.loadLogoFileAsResource(filename);

		String contentType = null;

		try {
//			Xác định kiểu nội dung resource
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (Exception ex) {
			throw new FileStorageException("Could not determine file type.");
		}
//			Nếu null thì gán ngầm định octet-stream
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

//			Trả về bao gồm (kiểu nội dung, header (kiểu file, phần đính kèm ), body phần nội dung).
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@GetMapping("/brand")
	public ResponseEntity<?> getBrands() {
		List<?> list = brandService.findAll();
		List<BrandDto> newList = list.stream().map(item -> {
			BrandDto dto = new BrandDto();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).collect(Collectors.toList());

		return new ResponseEntity<>(newList, HttpStatus.OK);
	}
	@GetMapping("/brand/find")
	public ResponseEntity<?> getBrands(@RequestParam("query") String query, 
			@PageableDefault(size = 2, sort = "name", direction = Sort.Direction.ASC) Pageable pageable ) {
		Page<Brand> list = brandService.findByName(query, pageable);
		
		List<BrandDto> newList = list.getContent().stream().map(item -> {
			BrandDto dto = new BrandDto();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).collect(Collectors.toList());
		
		Page<BrandDto> newPage =  new PageImpl<BrandDto>(newList, pageable, list.getTotalElements());

		return new ResponseEntity<>(newPage, HttpStatus.OK);
	}

	@GetMapping("/brand/page")
	public ResponseEntity<?> getBrands(
			@PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<Brand> list = brandService.findAll(pageable);
		List<BrandDto> newList = list.stream().map(item -> {
			BrandDto dto = new BrandDto();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).collect(Collectors.toList());

		return new ResponseEntity<>(newList, HttpStatus.OK);
	}

	@GetMapping("/brand/{id}/get")
	public ResponseEntity<?> getBrand(@PathVariable Long id) {

		var entity = brandService.findById(id);

		BrandDto dto = new BrandDto();
		BeanUtils.copyProperties(entity, dto);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@DeleteMapping("/brand/{id}")
	public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
		
		Optional<Brand> found= brandRepository.findById(id);
		fileStorageService.deleteLogoFile(found.get().getLogo());
		brandService.deleteId(id);

		return new ResponseEntity<>("Brand with id " + id + " was deleted", HttpStatus.OK);
	}

	@PatchMapping(value = "/brand/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateBrand(@PathVariable Long id, @Valid @ModelAttribute BrandDto dto,
			BindingResult result) {

		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);

		if (responseEntity != null) {
			return responseEntity;
		}
		Brand entity = brandService.updateBrand(id, dto);

		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setLogo(entity.getLogo());

		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}
}
