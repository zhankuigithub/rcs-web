package com.longmaster.admin.dto.menu;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * author zk
 * date 2021/2/26 13:59
 * description TODO
 */
@Getter
@Setter
@ToString
public class MenuAuditDTO {

    private List<Long> carrierIds;

    private Long appId;

}
