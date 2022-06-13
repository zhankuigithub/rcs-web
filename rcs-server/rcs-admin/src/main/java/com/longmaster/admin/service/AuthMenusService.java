package com.longmaster.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.admin.dto.auth.AuthMenusTreeDTO;
import com.longmaster.admin.dto.auth.MenusTreeDTO;
import com.longmaster.admin.entity.AuthMenus;

import java.util.List;


public interface AuthMenusService extends IService<AuthMenus> {

    MenusTreeDTO getMenusTagRole(Long parentId, Long roleId);

    List<AuthMenus> getMenus(Long parentId);

    Boolean delMenu(Long id);

    List<AuthMenusTreeDTO> treeList(boolean isShowHidden);
}
