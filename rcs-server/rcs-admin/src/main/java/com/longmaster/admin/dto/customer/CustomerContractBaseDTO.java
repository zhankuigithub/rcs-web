package com.longmaster.admin.dto.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CustomerContractBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "合同编号")
    @NotBlank(message = "请填写合同编号", groups = {CT.class})
    private String contractNo;

    @ApiModelProperty(value = "合同名称")
    @NotBlank(message = "请填写合同名称", groups = {CT.class})
    private String name;

    @ApiModelProperty(value = "合同生效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "请填写合同生效时间", groups = {CT.class})
    private LocalDateTime effectiveDt;

    @ApiModelProperty(value = "合同到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "请填写合合同到期时间", groups = {CT.class})
    private LocalDateTime expireDt;

    @ApiModelProperty(value = "合同是否续签（1是 2否）")
    @NotNull(message = "请选择合同是否续签", groups = {CT.class})
    private Integer renewStatus;

    @ApiModelProperty(value = "合同续签时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime renewDt;

    @ApiModelProperty(value = "合同附件地址")
    @NotBlank(message = "请上传合同附件", groups = {CT.class})
    private String accessoryUrl;

    @ApiModelProperty(value = "状态（0正常 1无效）")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
