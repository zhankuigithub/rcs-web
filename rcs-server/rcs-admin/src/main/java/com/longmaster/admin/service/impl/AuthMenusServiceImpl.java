package com.longmaster.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.admin.cache.Session;
import com.longmaster.admin.dto.auth.AuthMenusTreeDTO;
import com.longmaster.admin.dto.auth.MenusTreeDTO;
import com.longmaster.admin.dto.auth.PermissionMenusDTO;
import com.longmaster.admin.entity.AuthMenus;
import com.longmaster.admin.entity.AuthRolePermission;
import com.longmaster.admin.enums.MenusOperate;
import com.longmaster.admin.mapper.AuthMenusMapper;
import com.longmaster.admin.service.AuthMenusService;
import com.longmaster.admin.service.AuthRolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class AuthMenusServiceImpl extends ServiceImpl<AuthMenusMapper, AuthMenus> implements AuthMenusService {

    @Resource
    private AuthRolePermissionService rolePermissionService;

    private static final String ADMIN_ROLE_CODE = "SUPER_ADMIN";

    @Override
    public List<AuthMenus> getMenus(Long parentId) {
        return this.list(new LambdaQueryWrapper<AuthMenus>()
                .eq(Objects.nonNull(parentId), AuthMenus::getPid, parentId)
                .isNull(Objects.isNull(parentId), AuthMenus::getPid)
                .orderByDesc(AuthMenus::getSort, AuthMenus::getPid)
        );
    }

    @Override
    @Transactional
    public Boolean delMenu(Long id) {
        boolean isRm1 = this.removeById(id);
        boolean isRm2 = rolePermissionService.remove(new LambdaQueryWrapper<AuthRolePermission>().eq(AuthRolePermission::getMenusId, id));
        return isRm1 && isRm2;
    }

    @Override
    public List<AuthMenusTreeDTO> treeList(boolean isShowHidden) {
        LambdaQueryWrapper wrapper = null;
        if (!isShowHidden) {
            wrapper = new LambdaQueryWrapper<AuthMenus>().eq(AuthMenus::getHidden, 0);
        }
        List<AuthMenus> result = this.list(wrapper);
        List<AuthMenusTreeDTO> list = new ArrayList<>();
        for (AuthMenus authMenu : result) {
            AuthMenusTreeDTO tree = new AuthMenusTreeDTO();
            BeanUtil.copyProperties(authMenu, tree);
            list.add(tree);
        }
        return getTreeList(list);
    }

    @Override
    public MenusTreeDTO getMenusTagRole(Long parentId, Long roleId) {
        MenusTreeDTO menusTree = new MenusTreeDTO();
        buildMenus(null, roleId, menusTree.getTree(), menusTree.getChecked());
        return menusTree;
    }


    private void buildMenus(Long parentId, Long roleId, List<MenusTreeDTO.Node> menus, List<String> checked) {
        List<PermissionMenusDTO> parentMenus = baseMapper.getMenusTagRole(parentId, roleId, Session.isRole(new String[]{ADMIN_ROLE_CODE}));
        for (PermissionMenusDTO item : parentMenus) {
            if (item.getPid() == parentId || (Objects.nonNull(item.getPid()) && item.getPid().equals(parentId))) {
                MenusTreeDTO.Node node = new MenusTreeDTO.Node();
                node.setId(String.valueOf(item.getId()));
                node.setLabel(item.getTitle());
                node.setIcon(item.getIcon());
                node.setPermissions(item.getPermissions());
                node.setSelected(StrUtil.isNotBlank(item.getPermissions()));

                //生成按钮
                if (StrUtil.isNotBlank(item.getOperate())) {
                    String[] operates = item.getOperate().split(",");
                    for (String operate : operates) {
                        if (!StrUtil.isEmpty(operate)) {
                            MenusTreeDTO.Node button = new MenusTreeDTO.Node();
                            button.setId(node.getId() + "-" + operate);
                            button.setLabel(MenusOperate.valueOf(operate).getTitle());
                            button.setSelected(item.getPermissions() != null && item.getPermissions().indexOf(operate) >= 0);
                            node.getChildren().add(button);
                            if (button.getSelected()) {
                                checked.add(button.getId());
                            }
                        }
                    }
                } else {
                    buildMenus(item.getId(), roleId, node.getChildren(), checked);
                }
                menus.add(node);
            }
        }
    }

    /**
     * 获取父id
     */
    private List<AuthMenusTreeDTO> getTreeList(List<AuthMenusTreeDTO> entityList) {
        List<AuthMenusTreeDTO> resultList = new ArrayList<>();
        Long parentCode;
        for (AuthMenusTreeDTO entity : entityList) {
            parentCode = entity.getPid();
            if (parentCode == null) {
                resultList.add(entity);
            }
        }
        for (AuthMenusTreeDTO entity : resultList) {
            entity.setChildren(getSubList(entity.getId(), entityList));
        }
        return resultList.stream()
                .sorted(Comparator.comparing(AuthMenusTreeDTO::getSort)).collect(Collectors.toList());
    }

    /**
     * 获取子id
     */
    private List<AuthMenusTreeDTO> getSubList(Long id, List<AuthMenusTreeDTO> entityList) {
        List<AuthMenusTreeDTO> childList = new ArrayList<>();
        Long parentID;
        for (AuthMenusTreeDTO entity : entityList) {
            parentID = entity.getPid();
            if (id.equals(parentID)) {
                childList.add(entity);
            }
        }
        for (AuthMenusTreeDTO entity : childList) {
            entity.setChildren(getSubList(entity.getId(), entityList));
        }
        if (childList.size() == 0) {
            return null;
        }
        return childList.stream()
                .sorted(Comparator.comparing(AuthMenusTreeDTO::getSort)).collect(Collectors.toList());
    }

}
