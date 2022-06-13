package com.longmaster.platform.dto.scene;

import lombok.Data;


/**
 * author zk
 * date 2021/5/10 17:51
 * description 字节点
 */
@Data
public class EdgeDTO {

    private String source;
    private String target;
    private String sourceNode;
    private String targetNode;
    private Boolean arrow;
    private Double arrowPosition;

}
