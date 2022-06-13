package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_payload_data")
@ApiModel(value = "PayloadData对象", description = "建议回复类型消息的payload，data映射表")
public class PayloadData extends CommonEntity {

    @ApiModelProperty(value = "payload中的data值")
    @TableField("data")
    private String data;

    @ApiModelProperty("菜单id")
    @TableField("menu_id")
    private Long menuId;

    @ApiModelProperty("悬浮菜单id")
    @TableField("suggestion_id")
    private Long suggestionId;

    @ApiModelProperty("卡片id")
    @TableField("card_id")
    private Long cardId;

    @ApiModelProperty("消息模板id")
    @TableField("message_template_id")
    private Long messageTemplateId;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
