package com.longmaster.platform.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author zk
 * date 2021/2/22 14:29
 * description rcs消息实体
 */
@Getter
@Setter
@ToString
public class MessageDTO {

    // rcs需要的
    @JsonProperty("chatBotId")
    private String chatBotId;

    @JsonProperty("messageTmpId")
    private Long messageTmpId;

    @JsonProperty("userPhone")
    private String userPhone;

    @JsonProperty("isMessageBack")
    private Boolean isMessageBack;

}
