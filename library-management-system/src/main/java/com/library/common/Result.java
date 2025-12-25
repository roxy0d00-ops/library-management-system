package com.library.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code; // 200-成功
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "操作成功";
        result.data = data;
        return result;
    }

    public static Result<?> failure(int code, String message) {
        Result<?> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    public static Result<?> failure(String message) {
        return failure(500, message);
    }
}