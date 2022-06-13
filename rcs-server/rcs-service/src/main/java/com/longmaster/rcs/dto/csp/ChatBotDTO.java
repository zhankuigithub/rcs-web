package com.longmaster.rcs.dto.csp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatBotDTO {

    @NotBlank(message = "参数cspCode不能为空", groups = Update.class)
    private String cspCode;

    @NotBlank(message = "参数cspEcNo不能为空")
    private String cspEcNo;

    //@NotBlank(message = "参数accessTagNo不能为空")
    private String accessTagNo;

    @NotBlank(message = "参数chatbotId不能为空")
    private String chatBotId;

    @NotBlank(message = "参数serviceName不能为空")
    private String serviceName;

    @NotBlank(message = "参数serviceIcon不能为空")
    private String serviceIcon;

    @NotBlank(message = "参数serviceDescription不能为空")
    private String serviceDescription;

    @JsonProperty("SMSNumber")
    @NotBlank(message = "参数SMSNumber不能为空")
    private String SMSNumber;

    @NotBlank(message = "参数autograph不能为空")
    private String autograph;

    @NotEmpty(message = "参数category不能为空")
    private String[] category;

    @NotBlank(message = "参数provider不能为空")
    private String provider;

    @NotBlank(message = "参数showProvider不能为空")
    private String showProvider;

    @JsonProperty("TCPage")
    @NotBlank(message = "参数TCPage不能为空")
    private String tcPage;

    @NotBlank(message = "参数emailAddress不能为空")
    private String emailAddress;

    @NotBlank(message = "参数serviceWebsite不能为空")
    private String serviceWebsite;

    @NotBlank(message = "参数callBackNumber不能为空")
    private String callBackNumber;

    @NotBlank(message = "参数address不能为空")
    private String address;

    @NotBlank(message = "参数longitude不能为空")
    private String longitude;

    @NotBlank(message = "参数latitude不能为空")
    private String latitude;

    @NotEmpty(message = "参数ipWhiteList不能为空")
    private String[] ipWhiteList;

    public interface Update{

    }


    public String getSMSNumber() {
        return SMSNumber;
    }

    public void setSMSNumber(String SMSNumber) {
        this.SMSNumber = SMSNumber;
    }
}
