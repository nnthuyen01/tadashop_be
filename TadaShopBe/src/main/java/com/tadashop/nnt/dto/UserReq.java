package com.tadashop.nnt.dto;


import com.tadashop.nnt.utils.constant.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserReq {
	  private Long id;
	  @NotBlank(message = "First Name may not be blank")
	  private String firstname;
	  @NotBlank(message = "Last Name may not be blank")
	  private String lastname;
	  @Email(message = "Email invalidate")
	  @NotBlank(message = "Email may not be blank")
	  private String email;
	  @NotBlank(message = "Phone may not be blank")
	  private String phone;
	  @NotBlank(message = "User may not be blank")
	  private String username;
	  @NotBlank(message = "Password may not be blank")
	  @Size(min = 6,max = 26,message = "min = 6 and max 26")
	  private String password;
	  private Role role;
	  private Boolean enable;
	  
}
