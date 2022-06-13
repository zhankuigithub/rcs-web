package com.longmaster.platform.dto.message;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MaapFile {

    @ApiModelProperty("文件大小")
    private int fileSize;

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("文件url")
    private String url;

    @ApiModelProperty("下载好的url")
    private String mUrl;

    @ApiModelProperty("文件类型")
    private String contentType;
}
