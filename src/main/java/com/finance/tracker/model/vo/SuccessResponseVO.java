package com.finance.tracker.model.vo;

import lombok.Getter;

@Getter
public class SuccessResponseVO<T> {
    private final int code;
    private final String message;
    private final T data;


    private SuccessResponseVO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> SuccessResponseVO<T> of(int code, String message, T data) {
        return new SuccessResponseVO<>(code, message, data);
    }
}
