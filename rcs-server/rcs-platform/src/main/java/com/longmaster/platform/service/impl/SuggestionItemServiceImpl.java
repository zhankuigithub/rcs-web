package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Joiner;
import com.longmaster.platform.dto.menu.MenuWrapperWithCtDTO;
import com.longmaster.platform.entity.SuggestionItem;
import com.longmaster.platform.mapper.SuggestionItemMapper;
import com.longmaster.platform.service.PayloadDataService;
import com.longmaster.platform.service.SuggestionItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 建议浮动菜单项表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Slf4j
@Service
public class SuggestionItemServiceImpl extends ServiceImpl<SuggestionItemMapper, SuggestionItem> implements SuggestionItemService {

    @Resource
    private PayloadDataService payloadDataService;

    /**
     * @param action 结构化数据
     */
    @Override
    public SuggestionItem modifySuggestionItem(MenuWrapperWithCtDTO.Action action) {
        SuggestionItem item = action.transformSuggestionItem(action);
        this.saveOrUpdate(item);
        action.setId(item.getId());
        this.updatePayload(item);
        return item;
    }

    @Override
    public Long updateOrSaveSuggestionItem(SuggestionItem suggestionItem) {
        this.saveOrUpdate(suggestionItem);
        this.updatePayload(suggestionItem); // 更新内容
        return suggestionItem.getId();
    }

    @Override
    public void updatePayload(SuggestionItem suggestionItem) {
        if (suggestionItem.getType() != 1) {
            return;
        }
        try {
            String payload = suggestionItem.getPayload();
            String data = payloadDataService.updateOrSaveDataOfSug(payload, suggestionItem);
            baseMapper.updatePayload(data, suggestionItem.getId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSug(List<Long> ids) {
        if (ids.size() > 0) {
            String join = Joiner.on(",").join(ids);
            this.deleteSug(join);
        }
    }

    @Override
    public void deleteSug(long[] ids) {
        if (ids.length > 0) {
            List<Long> list = new ArrayList<>();
            for (long id : ids) {
                list.add(id);
            }
            String join = Joiner.on(",").join(list);
            this.deleteSug(join);
        }
    }

    @Override
    public void deleteSug(String idStr) {
        if (!StrUtil.isEmpty(idStr)) {
            baseMapper.deleteByIds(idStr);
            payloadDataService.deleteBySugIds(idStr);
        }
    }

}
