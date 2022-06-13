package com.longmaster.core.exception;

import com.longmaster.core.vo.Result;
import lombok.Getter;


public class ServerException extends RuntimeException {

    @Getter
    protected Result result = Result.SERVER_BUSY();

    public ServerException(String message) {
        super(message);
        result.setMsg(message);
    }

    public ServerException(String format, Object... args) {
        super(String.format(format, args));
        this.result.setMsg(String.format(format, args));
    }

    public ServerException(String message, Result result) {
        super(message);
        this.result = result;
    }
}
