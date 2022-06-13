package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.dynamicTemplate.DynamicTemplateDTO;
import com.longmaster.platform.dto.dynamicTemplate.DynamicTemplateQueryDTO;
import com.longmaster.platform.dto.dynamicTemplate.InteractionDynamicTemplateBodyDTO;
import com.longmaster.platform.dto.dynamicTemplate.KeysBodyDTO;
import com.longmaster.platform.entity.DynamicTemplate;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.List;

public interface DynamicTemplateService extends IService<DynamicTemplate> {

    IPage<DynamicTemplateDTO> pageQuery(PageParam<DynamicTemplateQueryDTO> pageParam, String carrierIds) throws JsonProcessingException;

    Object sendMessage(Long id, List<String> phones, JsonNode body, JsonNode suggestion, boolean isMessageBack) throws IOException, TemplateException;

    KeysBodyDTO getAllKeys(Long id) throws JsonProcessingException;

    DynamicTemplateDTO item(Long id);

    boolean add(DynamicTemplate entity);

    boolean update(DynamicTemplate entity);

    void interactionMessage(InteractionDynamicTemplateBodyDTO dynamicTemplateBody) throws JsonProcessingException;
}
