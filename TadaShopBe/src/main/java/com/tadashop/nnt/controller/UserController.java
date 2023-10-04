package com.tadashop.nnt.controller;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tadashop.nnt.dto.UpdateUserReq;
import com.tadashop.nnt.exception.FileStorageException;
import com.tadashop.nnt.model.User;
import com.tadashop.nnt.service.UserService;
import com.tadashop.nnt.service.filestorage.FileStorageService;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
	private final FileStorageService fileStorageService;

	@GetMapping("/user")
	public ResponseEntity<?> getUser() {
		User user = userService.getCurrentUser();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PutMapping("/user")
	public ResponseEntity<?> updateUser(@RequestBody UpdateUserReq userReq) {
		User usersUpdate = userService.updateUser(userReq);
		if (usersUpdate != null) {
			return new ResponseEntity<>(usersUpdate, HttpStatus.OK);
		} else
			return new ResponseEntity<>("User ID not exits", HttpStatus.NOT_FOUND);
	}

	@PutMapping(value = "/user/avatar", consumes = { "multipart/form-data" })
	public ResponseEntity<?> upAvatar(@RequestParam("img") MultipartFile file) throws IOException {
		String filename = userService.upAvartar(file);

		return new ResponseEntity<>(filename, HttpStatus.OK);
	}
	
	@GetMapping("/user/avatar/{filename:.+}")
	public ResponseEntity<?> downloadFile(@PathVariable String filename, HttpServletRequest request) {
		// Trả về đối tượng resource, và sử dụng resource để xác định kiểu nội dụng cho
		// file
		Resource resource = fileStorageService.loadAvatarFileAsResource(filename);

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

}
