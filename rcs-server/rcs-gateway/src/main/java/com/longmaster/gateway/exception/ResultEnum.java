package com.longmaster.gateway.exception;

import lombok.Getter;

/**
 * 统一响应枚举值
 */
@Getter
public enum ResultEnum {
    OK(201, "成功~"),
    SUCCESS(200, "操作成功~"),
    ILLEGAL_ARGS(202, "参数错误~"),
    FAILED(501, "操作失败~"),
    UN_AUTH(403, "授权异常，请重新退出登录或联系管理人员~"),
    NOT_TOKEN(404, "缺失令牌，无法访问服务~"),
    TOKEN_INVALID(405, "登录过期，请重新登录~"),
    TIME_OUT(501, "访问超时，请重新尝试~"),
    NOT_FIND(500, "错误请求，请检查路由~"),
    SERVER_BUSY(502, "服务繁忙，请稍后再试~"),
    BAD_NETWORK(503, "网络错误（404），请稍后重试或联系管理员~"),
    ERROR(500, "服务异常, 请稍后重试或联系管理人员~");

    private int code;

    private String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResultEnum codeOf(int code) {
        for (ResultEnum rs : ResultEnum.values()) {
            if (rs.code == code) {
                return rs;
            }
        }
        return null;
    }
}
