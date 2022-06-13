package com.longmaster.platform.dto.phoneBook;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;
import java.util.List;

@Data
public class PhoneNumberModifyDTO {

    public interface Update extends Default {

    }

    private static final String PHONE_REG = "1[3456789]\\d{9}";

    @ApiModelProperty(value = "id")
    @NotNull(message = "请选择记录", groups = {Update.class})
    private Long id;

    @ApiModelProperty(value = "客户id")
    @NotNull(message = "请选择客户")
    private Long customerId;

    @ApiModelProperty(value = "联系人姓名")
    private String name;

    @ApiModelProperty(value = "联系人电话")
    @Pattern(regexp = PHONE_REG, message = "请填写11位有效联系电话")
    private String phoneNum;

    @ApiModelProperty(value = "性别（1男 2女）")
    private Integer sex;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "标签ids")
    private List<Long> labelIds;

    @ApiModelProperty(value = "ids")
    private List<Long> applicationIds;
}
