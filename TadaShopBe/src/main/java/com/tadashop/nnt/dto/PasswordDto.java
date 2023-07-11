package com.tadashop.nnt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordDto {
    @NotBlank(message = "Email may not be blank")
    private String email;
    private String token;
    private String oldPassword;
    @NotBlank(message = "New Password may not be blank")
    private String newPassword;
}
