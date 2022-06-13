package com.longmaster.platform.dto.csp;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CallbackChatBotDTO extends ChatBotWithCtDTO {

    //通知类型，1：新增，2：变更，3: 删除
    private Integer type;
}
