package com.smartspend.smartspends.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponseDTO {
    
    private Long userId;
    private String username;
    private String mobileNumber;
    private String email;
    private String role;
    private String message;
}
