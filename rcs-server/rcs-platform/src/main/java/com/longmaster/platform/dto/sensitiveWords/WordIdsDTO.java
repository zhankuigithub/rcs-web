package com.longmaster.platform.dto.sensitiveWords;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class WordIdsDTO {

    @ApiModelProperty(value = "id集合")
    @Size(min = 1, message = "请至少选择一行记录")
    private List<Integer> ids;

}
