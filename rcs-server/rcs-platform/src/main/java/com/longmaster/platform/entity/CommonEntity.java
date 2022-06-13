package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author dengshuihong
 * @since 2021-02-22
 * 公共字段
 */
@Data
public class CommonEntity extends SuperEntity {

    protected static final String NET_URL_HTTPS = "^https://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";

    protected static final String IP_V4 = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
    //电话正则
    protected static final String PHONE_REG = "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";

    @ApiModelProperty(value = "数据主键， ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "主键不允许为空~", groups = {Update.class, Delete.class})
    private Long id;

    @ApiModelProperty(value = "创建时间")
    @TableField("insert_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDt;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;

    public interface CT {
    }

    public static interface CtCreate {
    }

    public interface CM {
    }
}
