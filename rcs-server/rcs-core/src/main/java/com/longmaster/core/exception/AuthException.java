package com.longmaster.core.exception;


import com.longmaster.core.vo.Result;

/**
 * @desc 权限异常类
 * @author dengshuihong
 * @date 2020-10-20
 */
public class AuthException extends ServerException {

    public AuthException(String message) {
        super(message, Result.UN_AUTH(message));
    }

    public AuthException(String format, Object... args) {
        super(format, args);
    }


    public AuthException(String message, Result result) {
        super(message, result);
    }
}
