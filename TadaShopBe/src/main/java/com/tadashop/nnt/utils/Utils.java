package com.tadashop.nnt.utils;

import org.springframework.security.core.context.SecurityContextHolder;

import com.tadashop.nnt.model.User;

public class Utils {

    public static Long getIdCurrentUser(){
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String id;
		if (principal instanceof User) {
		    id = ((User) principal).getId().toString();
		} else {
		    id = principal.toString();
		}
		return Long.valueOf(id);

    }
}
