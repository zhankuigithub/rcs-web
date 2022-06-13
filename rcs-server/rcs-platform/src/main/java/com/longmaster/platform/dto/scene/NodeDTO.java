package com.longmaster.platform.dto.scene;

import lombok.Data;

/**
 * author zk
 * date 2021/5/10 17:49
 * description 节点
 */
@Data
public class NodeDTO {

    private String id; // 不是数据库的id
    private String label;
    private String className;
    private String iconType;
    private int top;
    private int left;
    private int type;
    private String data;

}
