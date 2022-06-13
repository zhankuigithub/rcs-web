package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateAddDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateQueryDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateUpdDTO;
import com.longmaster.platform.entity.MessageTemplate;

/**
 * <p>
 * 消息模板 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface MessageTemplateService extends IService<MessageTemplate> {

    /**
     * 分页
     * @param pageParam
     * @return
     */
    IPage<MessageTemplateDTO> pageQuery(PageParam<MessageTemplateQueryDTO> pageParam, String carrierIds);

    /**
     * 获取审核状态
     * @param messageTemplate
     * @param carrierIds
     * @return
     */
    ArrayNode getAuditStatusOfMessageTemplate(MessageTemplate messageTemplate, String carrierIds);

    /**
     * 模板详情
     * @param id
     * @param carrierIds
     * @return
     */
    MessageTemplateDTO getOne(Long id, String carrierIds);

    /**
     * 新增模板
     * @param request
     * @throws JsonProcessingException
     */
    void addMessageTemplate(MessageTemplateAddDTO request) throws JsonProcessingException;

    /**
     * 修改模板
     * @param request
     */
    void updMessageTemplate(MessageTemplateUpdDTO request);

    /**
     * 删除模板
     * @param id
     * @return
     */
    void deleteMessageTemplate(Long id);
}
