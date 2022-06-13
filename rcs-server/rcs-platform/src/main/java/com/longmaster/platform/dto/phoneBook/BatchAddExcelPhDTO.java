package com.longmaster.platform.dto.phoneBook;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BatchAddExcelPhDTO {

    @ApiModelProperty(value = "客户id")
    private Long customerId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

    @ApiModelProperty(value = "标签ids")
    private List<Long> labelIds;

    @ApiModelProperty(value = "黑名单应用")
    private List<Long> applicationIds;

}
