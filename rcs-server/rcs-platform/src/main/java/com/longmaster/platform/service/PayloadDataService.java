package com.longmaster.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.platform.entity.MenuItem;
import com.longmaster.platform.entity.PayloadData;
import com.longmaster.platform.entity.SuggestionItem;

import java.util.List;

/**
 * author zk
 * date 2021/11/3 13:42
 * description 按钮->json内容 映射
 */
public interface PayloadDataService extends IService<PayloadData> {

    /**
     * 菜单保存data数据
     *
     * @param payload
     * @param entity
     * @return
     * @throws JsonProcessingException
     */
    String updateOrSaveDataOfMenu(String payload, MenuItem entity) throws JsonProcessingException;

    /**
     * 悬浮菜单保存data数据
     *
     * @param payload
     * @param suggestionItem
     * @return
     * @throws JsonProcessingException
     */
    String updateOrSaveDataOfSug(String payload, SuggestionItem suggestionItem) throws JsonProcessingException;


    /**
     * 重构payload
     *
     * @param payload 格式： {"reply":{"displayText":"你好","postback":{"data":"1455823823994118146"}}}
     * @return
     */
    String rebuildPayLoad(String payload);

    /**
     * 更新所属卡片id
     *
     * @param sugIds
     * @param cardId
     */
    void updateCardIds(List<Long> sugIds, Long cardId);


    /**
     * 更新所属消息模板id
     *
     * @param sugIds
     * @param templateId
     */
    void updateMessageTemplateIds(List<Long> sugIds, Long templateId);


    /**
     * 构建消息时，如果存在mid则直接返回，否则保存之后返回(为方便统计消息模板点击次数)
     *
     * @param payload
     * 示例：
     * 例如卡片被复用时，此时按钮未绑定消息模板，则：
     * 旧 {"reply":{"displayText":"在这里","postback":{"data":"1001"}}}  => 新 {"reply":{"displayText":"在这里","postback":{"data":"1002"}}}  // 返回新id的json（1002事先不存在）
     *
     *
     * 例如消息模板下的悬浮菜单，对应的按钮已经存在与消息模板对应的记录，则不新增，直接使用：
     * 旧 {"reply":{"displayText":"在这里","postback":{"data":"1001"}}}  => 新 {"reply":{"displayText":"在这里","postback":{"data":"1001"}}}  // 1001已经绑定了mid
     *
     * @param templateId
     * @return
     */
    String saveOrRebuildPayload(String payload, Long templateId);


    /**
     * 删除对应按钮id
     * @param sugIds
     */
    void deleteBySugIds(String sugIds);

    /**
     * 删除对应菜单
     * @param menuIds
     */
    void deleteByMenuIds(String menuIds);

}
