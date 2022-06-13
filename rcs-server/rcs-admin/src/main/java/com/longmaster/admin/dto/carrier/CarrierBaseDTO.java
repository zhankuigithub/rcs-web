package com.longmaster.admin.dto.carrier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CarrierBaseDTO extends BaseDTO {

    // id 约定
    public static final Long CMCC = 1L;
    public static final Long CUCC = 2L;
    public static final Long CTCC = 3L;
    public static final Long LONGMASTER = 4L;
    public static final Long GUANG_XI_CMCC = 5L;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "运营商名称")
    private String name;

    @ApiModelProperty(value = "运营商简介")
    private String details;

    @ApiModelProperty(value = "cspId（运营商分配）")
    private String cspId;

    @ApiModelProperty(value = "csp密匙")
    private String cspSecret;

    @ApiModelProperty(value = "csp初始化token")
    private String cspToken;

    @ApiModelProperty(value = "csp服务地址")
    private String cspServerUrl;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态 0正常 1停用")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
