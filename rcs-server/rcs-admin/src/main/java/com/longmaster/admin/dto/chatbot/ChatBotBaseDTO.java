package com.longmaster.admin.dto.chatbot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ChatBotBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属应用id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "请填写归宿应用ID(appId)", groups = {SuperEntity.Create.class, CT.class, CM.class})
    private Long appId;

    @ApiModelProperty(value = "运营商id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "请填写归宿运营商ID(carrierId)", groups = {SuperEntity.Create.class, CT.class, CM.class})
    private Long carrierId;

    @ApiModelProperty(value = "CSP编码")
    private String cspId;

    @ApiModelProperty(value = "机器人编码")
    @NotBlank(message = "参数机器人编码不允许为空(accessTagNo)", groups = {SuperEntity.Update.class})
    private String accessTagNo;

    @ApiModelProperty(value = "NCSP客户识别码")
    private String cspEcNo;

    @ApiModelProperty(value = "接入号码+自定义号码")
    @NotBlank(message = "请填写机器人编码（chatBotId）",  groups = {SuperEntity.Create.class, CT.class, CM.class})
    private String chatBotId;


    @ApiModelProperty(value = "接入号")
    private String accessNumber;

    @ApiModelProperty(value = "Chatbot 短信端口号=chatBotId 中(接 入号码+自定义号码)")
    @NotBlank(message = "请填写短信端口号（smsCode）", groups = CT.class)
    private String smsCode;

    @ApiModelProperty(value = "状态 0 停用， 1.启用")
    private Integer status;

    @ApiModelProperty(value = "状态（-2未通过 -1审核中 0下线 1上线）")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核内容")
    private String reviewData;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime auditDt;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
