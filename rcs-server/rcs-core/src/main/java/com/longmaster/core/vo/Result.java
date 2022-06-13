package com.longmaster.core.vo;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.longmaster.core.enums.ResultEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result<T> {
    @ApiModelProperty(value = "响应码")
    private Integer code;

    @ApiModelProperty(value = "响应消息")
    private String msg;

    @ApiModelProperty(value = "响应结果")
    private T data;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(ResultEnum rs, String msg) {
        this(rs, msg, null);
    }

    public Result(ResultEnum rs, T data) {
        this(rs, null, data);
    }

    public Result(ResultEnum rs, String msg, T data) {
        this.code = rs.getCode();
        this.msg = StrUtil.isNotBlank(msg) ? msg : rs.getMsg();
        this.data = data;
    }

    public static Result SUCCESS() {
        return SUCCESS(null);
    }

    public static Result SUCCESS(String msg) {
        return SUCCESS(msg, null);
    }

    public static Result SUCCESS(Object data) {
        return SUCCESS(null, data);
    }

    public static Result SUCCESS(String msg, Object data) {
        return new Result(ResultEnum.SUCCESS, msg, data);
    }

    public static Result ERROR(String msg) {
        return new Result(ResultEnum.ERROR, msg);
    }

    public static Result FAILED() {
        return FAILED(null);
    }

    public static Result FAILED(String msg) {
        return new Result(ResultEnum.FAILED, msg);
    }

    public static Result UN_AUTH(String msg) {
        return new Result(ResultEnum.UN_AUTH, msg);
    }

    public static Result NO_TOKEN() {
        return new Result(ResultEnum.NOT_TOKEN, "");
    }

    public static Result TOKEN_INVALID(String msg) {
        return new Result(ResultEnum.TOKEN_INVALID, msg);
    }

    public static Result SERVER_BUSY() {
        return SERVER_BUSY(null);
    }

    public static Result SERVER_BUSY(String detail) {
        return new Result(ResultEnum.SERVER_BUSY, detail);
    }
}
