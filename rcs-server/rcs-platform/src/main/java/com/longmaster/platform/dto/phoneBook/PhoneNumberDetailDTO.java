package com.longmaster.platform.dto.phoneBook;

import com.longmaster.platform.entity.PhoneNumberBook;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PhoneNumberDetailDTO extends PhoneNumberBook {

    @ApiModelProperty(value = "标签ids")
    private List<String> labelIds;

    @ApiModelProperty(value = "标签名称")
    private String labelNames;

    @ApiModelProperty(value = "黑名单应用ids")
    private List<String> appBlacklistIds;

    @ApiModelProperty(value = "黑名单名称")
    private String appBlacklistNames;

    private String sexTag;

    public String getSexTag() {
        return (getSex() == 1) ? "男" : "女";
    }
}
