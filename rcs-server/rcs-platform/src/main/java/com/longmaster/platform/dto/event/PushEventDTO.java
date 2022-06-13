package com.longmaster.platform.dto.event;

import com.baomidou.mybatisplus.annotation.TableField;
import com.longmaster.platform.entity.PushEvent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PushEventDTO extends PushEvent {

    @ApiModelProperty(value = "客户名称")
    @TableField("customerName")
    private String customerName;

    @ApiModelProperty(value = "应用名称")
    @TableField("appName")
    private String appName;

    @ApiModelProperty(value = "运营商")
    @TableField("carrierName")
    private String carrierName;

    @ApiModelProperty(value = "号码总量")
    @TableField("cnt")
    private Integer cnt;

}
