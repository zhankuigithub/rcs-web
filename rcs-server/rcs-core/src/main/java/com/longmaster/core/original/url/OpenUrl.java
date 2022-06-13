package com.longmaster.core.original.url;

import lombok.Data;

/**
 * author zk
 * date 2021/3/16 10:09
 * description 打开跳转链接json实体
 */
@Data
public class OpenUrl {
    private String url;
    private String application = "webview";
}
