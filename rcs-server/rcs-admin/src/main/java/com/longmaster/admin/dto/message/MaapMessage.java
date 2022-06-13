package com.longmaster.admin.dto.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * author zk
 * date 2021/5/13 15:02
 * description rcs传来，字段名依照rcs定义
 */
@Data
public class MaapMessage {

    public static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";
    public static final String CONTENT_TYPE_JSON = "application/vnd.gsma.botsuggestion.response.v1.0+json";
    public static final String CONTENT_TYPE_XML = "application/vnd.gsma.rcs-ft-http+xml";
    public static final String CONTENT_TYPE_CLIENT = "application/vnd.gsma.botsharedclientdata.v1.0+json";

    @ApiModelProperty("等同于chatbotid")
    private String destinationAddress;

    @ApiModelProperty("手机号")
    private String userPhone;

    @ApiModelProperty("内容格式")
    private String contentType;

    @ApiModelProperty("消息内容")
    private String bodyText;

    @ApiModelProperty("电信标识 conversationId")
    private String conversationId;

    @ApiModelProperty("电信标识 contributionId")
    private String contributionId;

    @ApiModelProperty("消息id")
    private String messageId;

    @ApiModelProperty("文件")
    private List<MaapFile> maapFile;// 文件对象（电信会存在2个，移动只有1个）
}
