package com.longmaster.core.exception;


import com.longmaster.core.vo.Result;

/**
 * Token 过期异常
 */
public class TokenInvalidException extends AuthException {

    public TokenInvalidException(String message) {
        super(message);
        this.result = Result.TOKEN_INVALID(message);
    }

    public TokenInvalidException(String format, Object... args) {
        super(format, args);
    }

    public TokenInvalidException(String message, Result result) {
        super(message, result);
    }
}
