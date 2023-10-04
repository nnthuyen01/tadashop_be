package com.tadashop.nnt.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.tadashop.nnt.dto.UpdateUserReq;

import com.tadashop.nnt.model.User;

public interface UserService {
	User getCurrentUser();
	
	User updateUser(UpdateUserReq userReq);
	
	List<?> findAll();
	
	Page<?> findAll(Pageable pageable);
	
	void disableUserById(Long userId);
	
	String upAvartar(MultipartFile file) throws IOException;
	
}
