package com.longmaster.platform.dto.material;

import com.longmaster.platform.entity.MaterialAuditRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MaterialAuditRecordDTO extends MaterialAuditRecord {

    @ApiModelProperty("运营商名称")
    private String carrierName;

}
