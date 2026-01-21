package com.smartspend.smartspends.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    
    private String token;
    private String mobileNumber;
    private String username;
    private String email;
    private String role;
    private String message;
}
