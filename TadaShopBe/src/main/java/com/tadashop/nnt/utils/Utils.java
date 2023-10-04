package com.tadashop.nnt.utils;

import org.springframework.security.core.context.SecurityContextHolder;

import com.tadashop.nnt.model.User;

public class Utils {

	public static Long getIdCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String id;
		if (principal instanceof User) {
			id = ((User) principal).getId().toString();
		} else {
			id = principal.toString();
		}
//		return Long.valueOf(id);
		if (id != null && id.matches("\\d+")) {
			return Long.valueOf(id);
		} else {
			// Xử lý trường hợp khi id không phải là một số hợp lệ.
			// Ví dụ: ném ra một exception, ghi log, hoặc xử lý khác.
			return null; // Hoặc một giá trị mặc định thích hợp.
		}

	}
}
