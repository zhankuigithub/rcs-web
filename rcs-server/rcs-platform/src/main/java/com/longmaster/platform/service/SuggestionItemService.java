package com.longmaster.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.platform.dto.menu.MenuWrapperWithCtDTO;
import com.longmaster.platform.entity.SuggestionItem;

import java.util.List;

/**
 * <p>
 * 建议浮动菜单项表 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface SuggestionItemService extends IService<SuggestionItem> {

    /**
     * 新增悬浮菜单
     *
     * @param action 结构化数据
     */
    SuggestionItem modifySuggestionItem(MenuWrapperWithCtDTO.Action action);


    Long updateOrSaveSuggestionItem(SuggestionItem suggestionItem);

    // 格式化按钮数据
    void updatePayload(SuggestionItem suggestionItem);

    void deleteSug(List<Long> ids);

    void deleteSug(long[] ids);

    /**
     * 删除按钮，同时删除data
     *
     * @param idStr
     */
    void deleteSug(String idStr);
}
