package com.leguan.base.exception;

import lombok.Data;

/**
 * @description 自定义异常类
 */

@Data
public class LexueException extends RuntimeException {
    private String errMessage;

    public LexueException() {
    }

    public LexueException(String message) {
        super(message);
        this.errMessage = message;
    }

    public static void cast(String message) {
        throw new LexueException(message);
    }

    public static void cast(CommonError error) {
        throw new LexueException(error.getErrMessage());
    }
}
