package com.longmaster.core.original.reply;

import lombok.Data;

/**
 * author zk
 * date 2021/3/16 10:06
 * description 建议回复的json
 */
@Data
public class Reply {

    private PostBack postback;
    private String displayText;

}
