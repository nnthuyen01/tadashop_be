package com.tadashop.nnt.service.iplm;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tadashop.nnt.dto.UpdateUserReq;

import com.tadashop.nnt.exception.AppException;

import com.tadashop.nnt.model.User;
import com.tadashop.nnt.repository.UserRepo;

import com.tadashop.nnt.service.UserService;
import com.tadashop.nnt.service.filestorage.FileStorageService;
import com.tadashop.nnt.utils.Utils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserIplm implements UserService {
	private final UserRepo userRepo;

	private final FileStorageService fileStorageService;

	@Override
	public User getCurrentUser() {
		Long userId = Utils.getIdCurrentUser();

		if (userId == null) {
			// Xử lý trường hợp khi userId không phải là số.
			// Ví dụ: ném ra một exception, ghi log, hoặc xử lý khác.
			throw new AppException("Not Found user");
		} else {
			User user = userRepo.findById(userId).orElseThrow(() -> new AppException("Not Found"));
			return user;

		}
	}

	@Override
	public User updateUser(UpdateUserReq userReq) {
		Optional<User> user = userRepo.findById(Utils.getIdCurrentUser());
		if (user.isPresent()) {
			User userUpdate = user.get();
			if (userReq.getFirstname() != null && !userReq.getFirstname().trim().equals(""))
				userUpdate.setFirstname(userReq.getFirstname().trim().replaceAll("  ", " "));
			if (userReq.getLastname() != null && !userReq.getLastname().trim().equals(""))
				userUpdate.setLastname(userReq.getLastname().trim().replaceAll("  ", " "));
			if (userReq.getPhone() != null && !userReq.getPhone().trim().equals(""))
				userUpdate.setPhone(userReq.getPhone().trim().replaceAll("  ", " "));
			userRepo.save(userUpdate);
			return userUpdate;
		} else
			return null;
	}

	@Override
	public List<?> findAll() {
		return userRepo.findAll();
	}

	@Override
	public Page<?> findAll(Pageable pageable) {
		return userRepo.findAll(pageable);
	}

	@Override
	public void disableUserById(Long userId) {
		User user = userRepo.findById(userId).orElseThrow(() -> new AppException("UserId = " + userId + "not found"));
		user.setEnable(!user.getEnable());
		userRepo.save(user);

	}

	@Override
	public String upAvartar(MultipartFile file) throws IOException {
		Long id = Utils.getIdCurrentUser();
		if (id == null) {
			// Xử lý trường hợp khi userId không phải là số.
			// Ví dụ: ném ra một exception, ghi log, hoặc xử lý khác.
			throw new AppException("Not Found user");
		} else {
			User user = userRepo.findById(id).orElseThrow(() -> new AppException("User ID not found"));

			String imgUrl = null;
			if (user.getAvatar() != null) {
//				imgUrl = user.getAvatar();
				fileStorageService.deleteAvatarFile(user.getAvatar());
				imgUrl = fileStorageService.storeAvatarFile(file);
			} else
				imgUrl = fileStorageService.storeAvatarFile(file);
			user.setAvatar(imgUrl);
			userRepo.save(user);
			return imgUrl;
		}

	}

	@Override
	public User findUserById(Long id) {		
		if (id == null) {			
			throw new AppException("Not Found user");
		} else {
			User user = userRepo.findById(id).orElseThrow(() -> new AppException("Not Found"));
			return user;
		}
	}

	@Override
    public Page<User> findByUsername(String username, Pageable pageable){
		return userRepo.findByUsernameContainsIgnoreCase(username, pageable);
	}
}
