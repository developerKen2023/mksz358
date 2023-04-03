package com.ken.mksz358.jwt.entity;

public enum ResponseCodeEnum {

    SUCCESS(200, "success"),
    FAIL(412, "fail"),
    LOGIN_ERROR(202, "invalidated username or password"),

    UNKNOWN_ERROR(500, "unknown error"),
    PARAMETER_ILLEGAL(400, "illegal parameter"),

    TOKEN_INVALID(412, "token 已过期或验证不正确！"),
    TOKEN_SIGNATURE_INVALID(403, "无效的签名"),
    TOKEN_MISSION(403, "token 缺失"),
    REFRESH_TOKEN_INVALID(412, "refreshToken 无效"),
    LOGOUT_ERROR(444, "用户登出失败");

    private final int code;
    private final String message;

    ResponseCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
