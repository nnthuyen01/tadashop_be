package com.tadashop.nnt.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tadashop.nnt.dto.ProductDetailResp;
import com.tadashop.nnt.dto.ProductDto;
import com.tadashop.nnt.dto.ProductImageDto;
import com.tadashop.nnt.exception.FileStorageException;
import com.tadashop.nnt.service.ProductService;
import com.tadashop.nnt.service.filestorage.FileStorageService;
import com.tadashop.nnt.service.iplm.MapValidationErrorService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/")
@CrossOrigin

public class ProductController {
	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private ProductService productService;

	@Autowired
	MapValidationErrorService mapValidationErrorService;

	@PreAuthorize("hasAuthority('admin:create')")
	@PostMapping("/admin/product")
	public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDto dto, BindingResult result) {
		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);

		if (responseEntity != null) {
			return responseEntity;
		}

		var savedDto = productService.insertProduct(dto);
		return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
	}

	@PreAuthorize("hasAuthority('admin:update')")
	@PatchMapping(value = "/admin/product/{id}")
	public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto dto,
			BindingResult result) {
		System.out.println("update product");
		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);

		if (responseEntity != null) {
			return responseEntity;
		}
		var updateDto = productService.updateProduct(id, dto);

		return new ResponseEntity<>(updateDto, HttpStatus.CREATED);
	}

	@GetMapping("/products/find")
	public ResponseEntity<?> getProductBriefsByName(@RequestParam("query") String query,
			@PageableDefault(size = 2, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

		return new ResponseEntity<>(productService.getProductBriefsByName(query, pageable), HttpStatus.OK);
	}

	@PostMapping(value = "/products/images/one", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE,
			MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile imageFile) {

		var fileInfo = fileStorageService.storeUploadedProductImageFile(imageFile);
		ProductImageDto dto = new ProductImageDto();

		BeanUtils.copyProperties(fileInfo, dto);
		dto.setStatus("done");
		dto.setUrl("http://localhost:8080/api/v1/products/images/" + fileInfo.getFileName());

		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}

	@GetMapping("/products/list")
	public ResponseEntity<?> getProducts() {
		return new ResponseEntity<>(productService.getProducts(), HttpStatus.OK);
	}

	@GetMapping("/products/images/{filename:.+}")
	public ResponseEntity<?> downloadFile(@PathVariable String filename, HttpServletRequest request) {
		// Trả về đối tượng resource, và sử dụng resource để xác định kiểu nội dụng cho
		// file
		Resource resource = fileStorageService.loadLogoProductImageFileAsResource(filename);

		String contentType = null;

		try {
			// Xác định kiểu nội dung resource
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (Exception ex) {
			throw new FileStorageException("Could not determine file type.");
		}
		// Nếu null thì gán ngầm định octet-stream
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		// Trả về bao gồm (kiểu nội dung, header (kiểu file, phần đính kèm ), body phần
		// nội dung).
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@DeleteMapping("/product/{id}")
	public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
		productService.deleteProductById(id);

		return new ResponseEntity<>("Product with ID: " + id + " was deleted", HttpStatus.OK);
	}

	@DeleteMapping("/product/images/{filename:.+}")
	public ResponseEntity<?> deleteImage(@PathVariable String filename) {
		fileStorageService.deleteProductImageFile(filename);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/product/{id}/getedit")
	public ResponseEntity<?> getEditedProduct(@PathVariable Long id) {
		return new ResponseEntity<>(productService.getEditedProductById(id), HttpStatus.OK);
	}

	@Operation(summary = "get product by ID")
	@GetMapping("/product/detail/{id}")
	public ResponseEntity<?> getProductById(@PathVariable Long id) {
		ProductDetailResp productResp = productService.findProductById(id);
		if (productResp != null) {
			return new ResponseEntity<>(productResp, HttpStatus.OK);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found Product ID");
//		return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);
	}

	@GetMapping("/products/findLeague")
	public ResponseEntity<?> getProductByleague(@RequestParam("query") String query,
			@PageableDefault(size = 2, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

		return new ResponseEntity<>(productService.getProductByLeague(query, pageable), HttpStatus.OK);
	}

	@GetMapping("/products/list/queryName")
	public ResponseEntity<?> getProductsByQueryname(@RequestParam("query") String query) {
		return new ResponseEntity<>(productService.getProductsByQueryName(query), HttpStatus.OK);
	}

	@GetMapping("/products/list/relate/{id}")
	public ResponseEntity<?> getProductsRelate(@PathVariable Long id) {
		return new ResponseEntity<>(productService.getProductsRelate(id), HttpStatus.OK);
	}



	@GetMapping("/products/listFilter")
	public ResponseEntity<?> getProductsFilter(@RequestParam(value = "brand", required = false) List<String> brand,
			@RequestParam(value = "kitType", required = false) List<String> kitType, @RequestParam(value = "gender", required = false) List<String> gender,
			@RequestParam("minPrice") Optional<Double> minPrice, @RequestParam("maxPrice") Optional<Double> maxPrice,
			@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
		return new ResponseEntity<>(productService.getProductFilters(brand,kitType,gender,minPrice.orElse(null),maxPrice.orElse(null),pageable), HttpStatus.OK);
	}
}
