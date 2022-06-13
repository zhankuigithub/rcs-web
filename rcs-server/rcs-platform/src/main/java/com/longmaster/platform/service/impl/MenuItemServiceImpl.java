package com.longmaster.platform.service.impl;

import com.longmaster.platform.entity.MenuItem;
import com.longmaster.platform.mapper.MenuItemMapper;
import com.longmaster.platform.service.MenuItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 固定菜单项表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Service
public class MenuItemServiceImpl extends ServiceImpl<MenuItemMapper, MenuItem> implements MenuItemService {

}
