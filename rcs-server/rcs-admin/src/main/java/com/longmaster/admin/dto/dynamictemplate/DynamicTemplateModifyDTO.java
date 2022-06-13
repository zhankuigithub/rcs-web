package com.longmaster.admin.dto.dynamictemplate;

import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DynamicTemplateModifyDTO extends SuperEntity {

    @ApiModelProperty(value = "主键id")
    @NotNull(message = "请填写主键id", groups = Update.class)
    private Long id;

    @ApiModelProperty(value = "所属应用id")
    @NotNull(message = "请填写所属应用id")
    private Long appId;

    @ApiModelProperty(value = "模板名称")
    @NotBlank(message = "请填写模板名称")
    private String name;

    @ApiModelProperty(value = "模板类型（1文本消息，2单卡片，3多卡片，4地理消息 5音频 6视频 7图片）")
    @NotNull(message = "请选择模板类型")
    private Integer type;

    @ApiModelProperty(value = "消息内容")
    @NotBlank(message = "请填写消息内容")
    private String payload;

    @ApiModelProperty(value = "短信内容")
    private String smsContent;
}
