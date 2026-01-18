package com.smartspend.smartspends.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String message;
    private String status;
    private T data;

    public ApiResponse(String message, String status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }
}
