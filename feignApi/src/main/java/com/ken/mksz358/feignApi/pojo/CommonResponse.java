package com.ken.mksz358.feignApi.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse<T> {

    private Integer code;
    private String message;
    private T data;
    private int dataSize;

    public CommonResponse(Integer code, String message) {
        this(code, message, null, 0);
    }

    public CommonResponse(Integer code, String message, T data) {
        this(code, message, data, 0);
    }
}
