package com.smartspend.smartspends.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String status;
    private String message;
    private String error;

    public ErrorResponse(String status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }
}
