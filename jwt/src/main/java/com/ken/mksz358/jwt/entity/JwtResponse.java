package com.ken.mksz358.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse<T> {

    private Integer code;
    private String message;
    private T data;
    private int dataSize;

    public JwtResponse(Integer code, String message) {
        this(code, message, null, 0);
    }

    public JwtResponse(Integer code, String message, T data) {
        this(code, message, data, 0);
    }
}
