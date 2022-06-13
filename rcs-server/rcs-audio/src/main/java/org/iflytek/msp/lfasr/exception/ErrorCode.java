package org.iflytek.msp.lfasr.exception;

public class ErrorCode {

    public static final String LFASR_INNER_ERR = "{\"ok\":\"-1\", \"errNo\":\"26000\", \"failed\":\"转写sdk通用错误!\", \"data\":\"\"}";
    public static final String LFASR_PARAM_LOCAL_FILE_ERR = "{\"ok\":\"-1\", \"errNo\":\"26001\", \"failed\":\"转写参数上传文件不能为空或文件不存在!\", \"data\":\"\"}";
    public static final String LFASR_PARAM_SIGNATURE_ERR = "{\"ok\":\"-1\", \"errNo\":\"26002\", \"failed\":\"转写参数客户端生成签名错误!\", \"data\":\"\"}";
    public static final String LFASR_LOCAL_FILE_UPLOAD_SIZE_ERR = "{\"ok\":\"-1\", \"errNo\":\"26003\", \"failed\":\"转写本地文件上传超过限定大小500M!\", \"data\":\"\"}";
    public static final String LFASR_LOCAL_FILE_READ_ERR = "{\"ok\":\"-1\", \"errNo\":\"26004\", \"failed\":\"转写上传文件读取错误!\", \"data\":\"\"}";
    public static final String LFASR_HTTP_POST_ERR = "{\"ok\":\"-1\", \"errNo\":\"26005\", \"failed\":\"HTTP请求失败!\", \"data\":\"\"}";
    public static final String LFASR_UPLOAD_TIMEOUT_ERR = "{\"ok\":\"-1\", \"errNo\":\"26640\", \"failed\":\"连接超时!请检查您的网络\", \"data\":\"\"}";

    private ErrorCode() {
    }
}
