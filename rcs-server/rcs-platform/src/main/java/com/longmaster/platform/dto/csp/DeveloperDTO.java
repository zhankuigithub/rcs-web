package com.longmaster.platform.dto.csp;

import com.longmaster.platform.entity.ChatbotDeveloper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeveloperDTO {


    public DeveloperDTO(ChatbotDeveloper developer, String accessTagNo) {
        this.agreement = developer.getProtocol();
        this.token = developer.getToken();
        this.accessTagNo = accessTagNo;
        this.enable = developer.getEnable();
        this.url = developer.getNotifyUrl();
    }

    @NotBlank(message = "请提供token")
    private String token;

    @ApiModelProperty(value = "协议类型 1:http，2:https")
    @NotBlank(message = "请提供协议类型")
    private String agreement;

    @ApiModelProperty(value = "回调 URL 地址根目录 以 http:// 开头，IP+端口的形式，用来接收 下行消息状态报告以及消息通知")
    private String url;

    @ApiModelProperty(value = "接入号")
    @NotBlank(message = "请提供接入号")
    private String accessTagNo;

    @ApiModelProperty(value = "Chatbot 接口是否启用，1:启用， 0:不启用")
    @NotBlank(message = "请选择接口是否启用")
    private Integer enable;

    @ApiModelProperty(value = "开发者账号")
    private String appId;

    @ApiModelProperty(value = "开发者密码")
    private String appKey;
}
