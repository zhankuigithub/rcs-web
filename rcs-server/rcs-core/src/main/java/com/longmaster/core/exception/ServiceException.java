package com.longmaster.core.exception;


import com.longmaster.core.vo.Result;

/**
 * @author dengshuihong
 * @date 2020-10-20
 * @desc <p>业务逻辑异常</p>
 */
public class ServiceException extends ServerException {

    public ServiceException(String msg) {
        super(msg, Result.FAILED(msg));
    }

    public ServiceException(String format, Object... args) {
        super(format, args);
    }

    public ServiceException(String message, Result result) {
        super(message, result);
    }

}
