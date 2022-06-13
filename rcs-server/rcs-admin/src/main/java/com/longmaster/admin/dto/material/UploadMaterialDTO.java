package com.longmaster.admin.dto.material;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * author zk
 * date 2021/2/25 14:11
 * description 提交审核素材
 */
@Setter
@ToString
@Getter
public class UploadMaterialDTO {

    @ApiModelProperty("素材表的id号列表")
    private List<Long> ids;

    @ApiModelProperty("运营商id列表")
    private List<Long> carrierIds;

    @ApiModelProperty("上传类型（1 永久  2临时）")
    private Integer uploadMode;

}
