package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_access_certificate")
@ApiModel(value = "AccessCertificate对象", description = "访问凭证表")
public class AccessCertificate extends SuperEntity {

    @ApiModelProperty(value = "appId")
    @TableField("app_id")
    private String appId;

    @ApiModelProperty(value = "secret，通过appId md5加密2次生成")
    @TableField("secret")
    private String secret;

}
