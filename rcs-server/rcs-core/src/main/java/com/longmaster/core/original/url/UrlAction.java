package com.longmaster.core.original.url;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * author zk
 * date 2021/3/16 10:09
 * description 跳转链接实体类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UrlAction {

    private OpenUrl openUrl;
}
