package com.longmaster.platform.dto.message;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NotifyBodyDTO {

    @ApiModelProperty("chatBotId")
    private String chatBotId;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty(" 1. 预约挂号成功 2.取消挂号 3.图文订单支付成功 4.医生回复提醒 5.预约体检下单成功")
    private Integer modal;

    @ApiModelProperty("动态参数")
    private JsonNode params;

    @Data
    public static class RegSuccess {

        @ApiModelProperty("患者")
        private String patientName;
        @ApiModelProperty("医院")
        private String hospitalName;
        @ApiModelProperty("科室")
        private String hdeptName;
        @ApiModelProperty("就诊医生")
        private String expertName;

        //private String timeSection;
        @ApiModelProperty("就诊号")
        private String igsOrder;

        @ApiModelProperty("订单日期")
        private long shiftDate;

        @ApiModelProperty("流水号")
        private String igSerialNumber;

        @ApiModelProperty("source")
        private Integer source;
    }

    @Data
    public static class ImSuccess {

        @ApiModelProperty("source")
        private Integer source;

        @ApiModelProperty("订单id")
        private Long inquiryId;

        @ApiModelProperty("回答内容")
        private String content;

        @ApiModelProperty("医生姓名")
        private String docName;
    }

    @Data
    public static class PhysicalSuccess {

        @ApiModelProperty("订单id")
        private Long inquiryId;

        @ApiModelProperty("source")
        private Integer source;
    }

}


