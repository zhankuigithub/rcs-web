package com.longmaster.platform.dto.notice;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NoticeQueryDTO {

    @ApiModelProperty(value = "公告标题")
    private String title;

    @ApiModelProperty(value = "公告类型，1全体公告，2定向公告")
    private Integer type;

}
