package com.tadashop.nnt.service;

import java.io.IOException;

import com.tadashop.nnt.dto.AuthenticationRequest;
import com.tadashop.nnt.dto.AuthenticationResponse;
import com.tadashop.nnt.dto.UserReq;
import com.tadashop.nnt.model.User;
import com.tadashop.nnt.model.Verification;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

	User saveRegister(UserReq userReq);
	
	User saveAdmin(UserReq userReq);
	
	AuthenticationResponse authenticate(AuthenticationRequest request);
	
	String validateVerificationToken(String token, String email);
	
	void saveVerificationTokenForUser(User user, String token);
	
	Verification SendToken(String email);
	Verification ResendToken(String email);
	
	User findUserByEmail(String email);
	
	User validatePasswordResetToken(String token, String email);
	
	void changePassword(User user, String newPassword);
	
	boolean checkIfValidOldPassword(User user, String oldPassword);
	
	void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
