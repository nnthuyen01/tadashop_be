package com.tadashop.nnt.service.iplm;


import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tadashop.nnt.auth.TokenRepo;
import com.tadashop.nnt.auth.TokenType;
import com.tadashop.nnt.config.JwtService;
import com.tadashop.nnt.dto.AuthenticationRequest;
import com.tadashop.nnt.dto.AuthenticationResponse;
import com.tadashop.nnt.dto.UserReq;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Token;
import com.tadashop.nnt.model.User;
import com.tadashop.nnt.model.Verification;
import com.tadashop.nnt.repository.UserRepo;
import com.tadashop.nnt.repository.VerificationRepo;
import com.tadashop.nnt.service.AuthenticationService;
import com.tadashop.nnt.utils.constant.Role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationIplm implements AuthenticationService {
	private final UserRepo repository;
	private final PasswordEncoder passwordEncoder;
	private final TokenRepo tokenRepository;
	private final VerificationRepo verificationRepo;

	private final JwtService jwtService;
	@Autowired
	private final AuthenticationManager authenticationManager;

	public User saveRegister(UserReq userReq) {
		if (!GenericValidator.isEmail(userReq.getEmail()))
			throw new AppException("Wrong email");
		boolean check = repository.existsByEmail(userReq.getEmail());
		if (check) {
			throw new AppException("Email already exits");
		}
		boolean checkUsername = repository.existsByUsername(userReq.getUsername());
		if (checkUsername) {
			throw new AppException("Username already exits");
		}

		var user = User.builder(). firstname(userReq.getFirstname()).lastname(userReq.getLastname())
				.email(userReq.getEmail()).phone(userReq.getPhone()).username(userReq.getUsername())
				.password(passwordEncoder.encode(userReq.getPassword())).role(Role.USER).amountPaid(0.0).enable(false).build();

//        users.getAddresses().add(new Address(null,userReq.getAddress(),true,users));

		return repository.save(user);
	}

	public User saveAdmin(UserReq userReq) {
		if (!GenericValidator.isEmail(userReq.getEmail()))
			throw new AppException("Wrong email");
		boolean check = repository.existsByEmail(userReq.getEmail());
		if (check) {
			throw new AppException("Email already exits");
		}
		boolean checkUsername = repository.existsByUsername(userReq.getUsername());
		if (checkUsername) {
			throw new AppException("Username already exits");
		}

		var user = User.builder().firstname(userReq.getFirstname()).lastname(userReq.getLastname())
				.email(userReq.getEmail()).phone(userReq.getPhone()).username(userReq.getUsername())
				.password(passwordEncoder.encode(userReq.getPassword())).role(Role.ADMIN).amountPaid(0.0).enable(true).build();
		return repository.save(user);
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {

		Optional<User> tempUser = repository.findByUsername(request.getUsername());
		if (tempUser.isEmpty()) {
			throw new AppException("Username not found");
		} else if (!tempUser.get().getEnable()) {
			throw new AppException("Username not enable");
		} else {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

			var user = repository.findByUsername(request.getUsername()).orElseThrow();
			var jwtToken = jwtService.generateToken(user);
			var refreshToken = jwtService.generateRefreshToken(user);
			revokeAllUserTokens(user);
			saveUserToken(user, jwtToken);
			return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).username(user.getUsername()).role(user.getRole()).build();
		}
	}

	public String validateVerificationToken(String token, String email) {

		Verification verificationToken = verificationRepo.findVerificationTokenByTokenAndUser_Email(token, email);

		if (verificationToken == null) {
			return "invalid";
		}

		User user = verificationToken.getUser();
		Calendar cal = Calendar.getInstance();

		if ((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
			verificationRepo.delete(verificationToken);
			return "expired";
		}
		verificationToken.setToken(null);
		verificationRepo.save(verificationToken);
		user.setEnable(true);
		user.setRole(Role.USER);
		repository.save(user);

		return "valid";
	}

	public void saveVerificationTokenForUser(User user, String token) {
		Verification verificationToken = new Verification(user, token);
		System.out.println(verificationToken.getExpirationTime());
		verificationRepo.save(verificationToken);
	}

	private void saveUserToken(User user, String jwtToken) {
		var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false).revoked(false)
				.build();
		tokenRepository.save(token);
	}

	public Verification SendToken(String email) {
		Verification verificationToken = verificationRepo.findVerificationTokenByUserEmail(email);
		if (verificationToken != null) {
			String token = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
			verificationToken.setToken(token);
			verificationRepo.save(verificationToken);
			return verificationToken;
		}				
		return null;
	}
	public Verification ResendToken(String email) {
		Verification verificationToken = verificationRepo.findVerificationTokenByUserEmail(email);
		if (verificationToken != null) {
			String token = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
			verificationToken.setToken(token);
			verificationRepo.save(verificationToken);
			return verificationToken;
		} else {
			Optional<User> user = repository.findByEmail(email);
			User userEmail = user.get();
			String token = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
		    verificationToken = new Verification(userEmail, token);
		    
			verificationRepo.save(verificationToken);
			return verificationToken;
		}
	}

	public User findUserByEmail(String email) {
		var user = repository.findByEmail(email);
//		return user.get();
		if (user.isPresent()) {
	        return user.get();
	    } else {
	        // Xử lý trường hợp không tìm thấy người dùng cho địa chỉ email đã cho
	        // Bạn có thể ném ra một ngoại lệ, trả về một người dùng mặc định hoặc trả về null, phụ thuộc vào trường hợp sử dụng của bạn.
	        // Ví dụ:
//	        throw new NoSuchElementException("Không tìm thấy người dùng cho email: " + email);
	    	return null;
	    }
	}

	public User validatePasswordResetToken(String token, String email) {
		Verification verificationToken = verificationRepo.findVerificationTokenByTokenAndUser_Email(token, email);

		if (verificationToken == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();

		if ((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
//			verificationRepo.delete(verificationToken);
			return null;
		}
		User user = verificationToken.getUser();
		verificationToken.setToken(null);
		verificationRepo.save(verificationToken);
		return user;
	}

	public void changePassword(User user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		repository.save(user);
	}

	public boolean checkIfValidOldPassword(User user, String oldPassword) {
		return passwordEncoder.matches(oldPassword, user.getPassword());
	}

	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty())
			return;
		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUserTokens);
	}

	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userName;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		refreshToken = authHeader.substring(7);
		userName = jwtService.extractUsername(refreshToken);
		if (userName != null) {
			var user = this.repository.findByUsername(userName).orElseThrow();
			if (jwtService.isTokenValid(refreshToken, user)) {
				var accessToken = jwtService.generateToken(user);
				revokeAllUserTokens(user);
				saveUserToken(user, accessToken);
				var authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
						.build();
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			}
		}
	}

}
