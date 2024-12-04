package org.example.dto;

public class BaseResponseDto {
    private String message;
    private Object data;
    private int status;

    public BaseResponseDto() {}

    public BaseResponseDto(String message, Object data, int status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    // Getters and Setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
