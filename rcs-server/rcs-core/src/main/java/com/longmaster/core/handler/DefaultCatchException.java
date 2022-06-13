package com.longmaster.core.handler;

import com.longmaster.core.enums.ResultEnum;
import com.longmaster.core.exception.AuthException;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;

@Slf4j
@Lazy
@RestControllerAdvice
public class DefaultCatchException {

    private static final String TAG = "DefaultCatchException";
    private static final String PRINT = "[{} -> {}] try catch {} Exception, error msg is {}";

    public DefaultCatchException() {
        log.info("[{}] init Global Exception catch with default by Spring SPI... ", TAG);
    }

    /**
     * 非法参数处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public Result validTry(BindException e) {
        log.warn(PRINT, TAG, "BindException", e.getClass().getName(), e.getMessage());
        return new Result(ResultEnum.ILLEGAL_ARGS, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 入参校验拦截
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result validTry(MethodArgumentNotValidException e) {
        log.warn(PRINT, TAG, "MethodArgumentNotValidException", e.getClass().getName(), e.getMessage());
        return new Result(ResultEnum.ILLEGAL_ARGS, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 非法参数依赖拦截
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result validTry(IllegalArgumentException e) {
        log.warn(PRINT, TAG, "IllegalArgumentException", e.getClass().getName(), e.getMessage());
        return new Result(ResultEnum.ILLEGAL_ARGS, e.getMessage());
    }

    /**
     * 数据类型转换错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public Result validTry(MethodArgumentTypeMismatchException e) {
        log.warn(PRINT, TAG, "MethodArgumentTypeMismatchException", e.getClass().getName(), e.getMessage());
        return Result.FAILED("请求数据不合法，请检查你的参数类型~");
    }

    /**
     * 主键冲突拦截
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = DuplicateKeyException.class)
    public Result validTry(DuplicateKeyException e) {
        log.warn(PRINT, TAG, "DuplicateKeyException", e.getClass().getName(), e.getMessage());
        return Result.FAILED("操作失败，操作数据已存在，请刷新查看或联系管理人员~");
    }

    /**
     * 数据操作异常拦截
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = SQLException.class)
    public Result validTry(SQLException e) {
        log.error(PRINT, TAG, "SQLException", e.getClass().getName(), e.getMessage());
        return Result.ERROR("数据操作失败~");
    }

    /**
     * 自定义异常拦截
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = ServerException.class)
    public Result validTry(ServerException e) {
        log.error(PRINT, TAG, "ServerException", e.getClass().getName(), e.getMessage());
        if (e instanceof AuthException || e instanceof ServiceException || e instanceof ServerException) {
            return ((ServerException) e).getResult();
        }
        return Result.FAILED();
    }

    /**
     * 其他异常拦截
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Result validTry(Exception e) {
        log.error(PRINT, TAG, "Exception", e.getClass().getName(), e);
        return Result.ERROR(null);
    }
}
