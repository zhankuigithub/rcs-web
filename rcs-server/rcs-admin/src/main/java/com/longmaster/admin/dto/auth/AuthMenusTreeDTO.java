package com.longmaster.admin.dto.auth;

import com.longmaster.admin.entity.AuthMenus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AuthMenusTreeDTO extends AuthMenus {

    @ApiModelProperty(value = "字节点")
    private List<AuthMenusTreeDTO> children;

}
