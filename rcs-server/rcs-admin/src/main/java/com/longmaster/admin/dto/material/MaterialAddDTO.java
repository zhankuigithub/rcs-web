package com.longmaster.admin.dto.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * author zk
 * date 2021/3/2 18:37
 * description TODO
 */
@Data
public class MaterialAddDTO {

    @ApiModelProperty("应用id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty("客户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("文件类型（1 图片 2音频 3视频 4文章 5链接（存originalFileUrl））")
    private Integer type;

    @ApiModelProperty("文件地址")
    private String fileUrl;

    @ApiModelProperty("封面地址")
    private String thumbnailUrl;

    @ApiModelProperty("原始文件地址")
    private String originalFileUrl;

    @ApiModelProperty("原始封面地址")
    private String originalThumbnailUrl;

    @ApiModelProperty("html详情")
    private String content;

    @ApiModelProperty("审核状态 0未审核 1审核通过 2审核不通过")
    private Integer status = 1;

    @ApiModelProperty("归属 (1 csp后台 2 chatbot云平台)")
    private Integer attribution = 1;
}
