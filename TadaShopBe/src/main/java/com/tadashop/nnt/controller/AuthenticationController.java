package com.tadashop.nnt.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.dto.AuthenticationRequest;
import com.tadashop.nnt.dto.AuthenticationResponse;
import com.tadashop.nnt.dto.PasswordDto;
import com.tadashop.nnt.dto.UserReq;
import com.tadashop.nnt.model.User;
import com.tadashop.nnt.service.AuthenticationService;
import com.tadashop.nnt.service.email.EmailSenderService;
import com.tadashop.nnt.service.iplm.MapValidationErrorService;
import com.tadashop.nnt.utils.constant.EmailType;

import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;
	private final EmailSenderService emailSenderService;

	final String TITLE_SUBJECT_EMAIL = "TADA Register TOKEN";
	final String TYPE_REGIS = "confirm registration code";
	final String RESET_PASSWORD_TOKEN = "Reset Password Token";
	final String TYPE_RESET = "confirm password change code";

	@Autowired
	MapValidationErrorService mapValidationErrorService;

	@PostMapping("/registerUser")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserReq userReq, BindingResult result)
			throws MessagingException, TemplateException, IOException {

		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);

		if (responseEntity != null) {
			return responseEntity;
		}
		User user = service.saveRegister(userReq);

		userReq.setRole(user.getRole());
		userReq.setId(user.getId());

		String token = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
		service.saveVerificationTokenForUser(user, token);

		Map<String, Object> model = new HashMap<>();
		model.put("token", token);
		model.put("title", TITLE_SUBJECT_EMAIL);
		model.put("subject", TITLE_SUBJECT_EMAIL);
		model.put("type_of_action", TYPE_REGIS);
		
		emailSenderService.sendEmail(userReq.getEmail(), model, EmailType.REGISTER);

		return new ResponseEntity<>(userReq, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/verifyRegistration", method = RequestMethod.GET)
	public ResponseEntity<?> verifyRegistration(@RequestParam("token") String token,
			@RequestParam("email") String email) {
		String result = service.validateVerificationToken(token, email);
		if (!result.equals("valid")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(result);
	}

	@PostMapping("/registerAdmin")
	public ResponseEntity<?> registerAdmin(@Valid @RequestBody UserReq userReq, BindingResult result) {

		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);

		if (responseEntity != null) {
			return responseEntity;
		}
		User user = service.saveAdmin(userReq);
		userReq.setRole(user.getRole());
		userReq.setId(user.getId());
		return new ResponseEntity<>(userReq, HttpStatus.CREATED);
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(service.authenticate(request));
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<?> resetPassword(@RequestParam String email, HttpServletRequest request)
			throws MessagingException, TemplateException, IOException {
		User user = service.findUserByEmail(email);
		if (user != null && user.getEnable()) {
			String token = "";
			token = service.SendToken(email).getToken();
			Map<String, Object> model = new HashMap<>();
			model.put("token", token);
			model.put("title", RESET_PASSWORD_TOKEN);
			model.put("subject", RESET_PASSWORD_TOKEN);
			model.put("type_of_action", TYPE_RESET);
			
			// Send email
			emailSenderService.sendEmail(user.getEmail(), model, EmailType.REGISTER);
			log.info("Reset password: {}", token);
			return ResponseEntity.ok("Sent email reset token");
		}
		return ResponseEntity.badRequest().body("Not found email");
	}

	@PostMapping("/saveChangePassword")
	public ResponseEntity<?> savePassword(@Valid @RequestBody PasswordDto passwordDTO) {
		User result = service.validatePasswordResetToken(passwordDTO.getToken(), passwordDTO.getEmail());
		if (result == null) {

			return ResponseEntity.badRequest().body("Invalid token");
		}
		if (!result.getEnable()) {
			return ResponseEntity.ok().body("Email not verify");
		}
		if(!service.checkIfValidOldPassword(result,passwordDTO.getOldPassword())) {
            return ResponseEntity.badRequest().body("Invalid Old Password");
        }		
		service.changePassword(result, passwordDTO.getNewPassword());
		return ResponseEntity.ok().body("Change password successfully");
	}

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordDto passwordDTO){
        User user = service.findUserByEmail(passwordDTO.getEmail());
        if(!service.checkIfValidOldPassword(user,passwordDTO.getOldPassword())) {
            return ResponseEntity.badRequest().body("Invalid Old Password");
        }
        if(service.checkIfValidOldPassword(user,passwordDTO.getNewPassword())) {
            return ResponseEntity.badRequest().body("The new password is the same as the old password");
        }
        //Save New Password
        service.changePassword(user, passwordDTO.getNewPassword());
        return ResponseEntity.ok().body("Password Changed Successfully");
    }


	@PostMapping("/refresh-token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		service.refreshToken(request, response);
	}

}
