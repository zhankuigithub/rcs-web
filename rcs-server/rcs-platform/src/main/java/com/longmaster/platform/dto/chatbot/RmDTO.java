package com.longmaster.platform.dto.chatbot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RmDTO {

    @ApiModelProperty("记录id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "请填写id")
    private Long id;

    @ApiModelProperty("短信验证码")
    @NotBlank(message = "请填写验证码")
    private String code;

}
