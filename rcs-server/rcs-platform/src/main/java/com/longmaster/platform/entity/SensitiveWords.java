package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@TableName("t_sensitive_words")
@ApiModel(value = "SensitiveWords对象", description = "敏感词")
public class SensitiveWords extends SuperEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(message = "请填写id", groups = Update.class)
    private Integer id;

    @ApiModelProperty(value = "文字信息")
    @TableField(value = "word")
    @NotBlank(message = "文字信息不可以为空")
    private String word;

    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    @NotBlank(message = "备注不可以为空")
    private String remark;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

    @ApiModelProperty(value = "创建时间")
    @TableField("insert_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDt;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;

}
