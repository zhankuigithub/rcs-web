package com.longmaster.platform.dto.sensitiveWords;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class WordInfoDTO {

    @ApiModelProperty(value = "文字集合")
    @Size(min = 1, message = "请至少填写一个词语")
    private List<String> words;

    @ApiModelProperty(value = "备注")
    @NotBlank(message = "请填写备注")
    private String remark;

}
