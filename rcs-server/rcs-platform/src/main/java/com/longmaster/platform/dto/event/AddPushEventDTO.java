package com.longmaster.platform.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * author zk
 * date 2021/3/2 16:51
 * description 定时任务推送
 */
@Getter
@Setter
public class AddPushEventDTO {

    @ApiModelProperty("模板id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "请选择消息模板")
    private Long templateId;

    @ApiModelProperty("应用id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "请选择应用")
    private Long appId;

    @ApiModelProperty("用户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("任务名称")
    @NotBlank(message = "请填写任务名称")
    private String name;

    @ApiModelProperty("运营商id列表")
    @NotNull(message = "请选择运营商")
    @Size(min = 1, message = "请至少选择一个运营商")
    private List<Long> carrierIds;

    @ApiModelProperty("手机号列表")
    @NotNull(message = "请填写手机号")
    @Size(min = 1, message = "请至少填写一个手机号码")
    private List<String> userPhones;

    @ApiModelProperty("发送时间")
    @NotNull(message = "请填写发送时间")
    private String sendDt;

    @ApiModelProperty("备注")
    private String remark;

    public LocalDateTime getSendDt() {
        return LocalDateTime.parse(sendDt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
