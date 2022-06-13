package com.longmaster.admin.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author zk
 * date 2021/2/22 14:29
 * description TODO
 */
@Getter
@Setter
@ToString
public class MessageDTO {

    // rcs需要的
    @JsonProperty("chatBotId")
    private String chatBotId;

    @JsonProperty("messageTmpID")
    private Long messageTmpID;

    @JsonProperty("userPhone")
    private String userPhone;

}
