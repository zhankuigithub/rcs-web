package com.longmaster.platform.dto.csp;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.longmaster.core.base.SuperEntity;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ToString
public class ChatBotWithCtDTO {

    public ChatBotWithCtDTO() {
    }

    public ChatBotWithCtDTO(String ipWhiteStr, String categoryStr) {
        this.ipWhiteList = ipWhiteStr.split("\n");
        this.category = categoryStr.split("\n");
    }

    @Alias("cspId")
    @NotBlank(message = "参数cspCode不能为空")
    private String cspCode;

    @Alias("cspEcNo")
    @NotBlank(message = "参数cspEcNo不能为空")
    private String cspEcNo;

    @Alias("accessTagNo")
    @NotBlank(message = "参数accessTagNo不能为空", groups = SuperEntity.Update.class)
    private String accessTagNo;

    @NotBlank(message = "参数chatBotId不能为空")
    private String chatBotId;

    @Alias("name")
    @NotBlank(message = "参数serviceName不能为空")
    private String serviceName;

    @Alias("logoUrl")
    @NotBlank(message = "参数serviceIcon不能为空")
    private String serviceIcon;

    @Alias("details")
    @NotBlank(message = "参数serviceDescription不能为空")
    private String serviceDescription;

    @Alias("smsCode")
    @NotBlank(message = "参数SMSNumber不能为空")
    private String SMSNumber;

    @NotBlank(message = "参数autograph不能为空")
    private String autograph;

    @NotEmpty(message = "参数category不能为空")
    private String[] category;

    @Alias("providerName")
    @NotBlank(message = "参数provider不能为空")
    private String provider;

    @Alias("isShowProvider")
    @NotBlank(message = "参数showProvider不能为空")
    private String showProvider;

    @Alias("tosUrl")
    @JsonProperty("TCPage")
    @NotBlank(message = "参数TCPage不能为空")
    private String tcPage;

    @Alias("email")
    @NotBlank(message = "参数emailAddress不能为空")
    private String emailAddress;

    @Alias("websiteUrl")
    @NotBlank(message = "参数serviceWebsite不能为空")
    private String serviceWebsite;

    @Alias("phoneNum")
    @NotBlank(message = "参数callBackNumber不能为空")
    private String callBackNumber;

    @NotBlank(message = "参数address不能为空")
    private String address;

    @NotNull(message = "参数longitude不能为空")
    @Digits(integer = 5, fraction = 3, message = "仅支持小数点保留3位有效位")
    private Double longitude;

    @NotNull(message = "参数latitude不能为空")
    @Digits(integer = 5, fraction = 3, message = "仅支持小数点保留3位有效位")
    private Double latitude;

    @NotEmpty(message = "参数ipWhiteList不能为空")
    private String[] ipWhiteList;


    public interface Update {
    }
}
