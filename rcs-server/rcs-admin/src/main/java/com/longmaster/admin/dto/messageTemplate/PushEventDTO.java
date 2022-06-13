package com.longmaster.admin.dto.messageTemplate;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * author zk
 * date 2021/3/2 16:51
 * description TODO
 */
@Getter
@Setter
public class PushEventDTO {

    @ApiModelProperty("模板id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long templateId;

    @ApiModelProperty("应用id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty("用户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("任务名称")
    private String  name;

    @ApiModelProperty("运营商id列表")
    private List<Long> carrierIds;

    @ApiModelProperty("手机号列表")
    private List<String> userPhones;

    @ApiModelProperty("发送时间")
    private String sendDt;
}
