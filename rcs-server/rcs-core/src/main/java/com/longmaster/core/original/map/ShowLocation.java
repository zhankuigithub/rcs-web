package com.longmaster.core.original.map;

import lombok.Data;

@Data
public class ShowLocation {
    /**
     * 本地位置坐标
     */
    private Location location;
    /**
     * 打开的网址
     */
    private String fallbackUrl;
}
