package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 运营商信息表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_carrier")
@ApiModel(value="Carrier对象", description="运营商信息表")
public class Carrier extends CommonEntity {

    // id 约定
    public static final Long CMCC = 1L;
    public static final Long CUCC = 2L;
    public static final Long CTCC = 3L;
    public static final Long LONGMASTER = 4L;
    public static final Long GUANG_XI_CMCC = 5L;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "运营商名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "运营商简介")
    @TableField("details")
    private String details;

    @ApiModelProperty(value = "cspId（运营商分配）")
    @TableField("csp_id")
    private String cspId;

    @ApiModelProperty(value = "csp密匙")
    @TableField("csp_secret")
    private String cspSecret;

    @ApiModelProperty(value = "csp初始化token")
    @TableField("csp_token")
    private String cspToken;

    @ApiModelProperty(value = "csp服务地址")
    @TableField("csp_server_url")
    private String cspServerUrl;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "状态 0正常 1停用")
    @TableField("status")
    private Integer status;


    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
