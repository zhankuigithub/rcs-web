package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.longmaster.platform.entity.Carrier;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.entity.MaterialAuditRecord;
import com.longmaster.platform.enums.UploadModeEnum;
import com.longmaster.platform.mapper.ChatbotMapper;
import com.longmaster.platform.mapper.MaterialAuditRecordMapper;
import com.longmaster.platform.service.RcsService;
import com.longmaster.platform.util.UrlUtil;
import com.longmaster.core.exception.ServerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class CtUploadStrategy extends AbstractUploadStrategy {

    @Resource
    private MaterialAuditRecordMapper materialAuditRecordMapper;

    @Resource
    private ChatbotMapper chatbotMapper;

    @Resource
    private RcsService rcsService;

    @Override
    public Long getCarrierId() {
        return Carrier.CTCC;
    }

    @Override
    public void upload(Material material, int uploadMode) throws IOException {
        Long appId = material.getAppId();  // 获取电信下的该应用下的chatbot
        Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>()
                .eq(Chatbot::getAppId, appId)
                .eq(Chatbot::getCarrierId, Carrier.CTCC)
                .eq(Chatbot::getStatus, 1)
                .eq(Chatbot::getAuditStatus, 1)
                .select(Chatbot::getChatBotId));

        if (chatbot == null) {
            throw new ServerException("电信失败：此应用下没有正在启用的chatbot");
        }

        long count = materialAuditRecordMapper.
                selectCount(new LambdaQueryWrapper<MaterialAuditRecord>().
                        eq(MaterialAuditRecord::getChatBotId, chatbot.getChatBotId()).
                        eq(MaterialAuditRecord::getMaterialId, material.getId()));

        if (count > 0) {
            throw new ServerException("电信失败：此素材已经提交过审核了");
        }

        String sourceUrl = UrlUtil.buildMinIoURL(material.getSourceUrl());

        JsonNode jsonNode = rcsService.ctUploadChatBotMaterial(chatbot.getChatBotId(), sourceUrl, UploadModeEnum.getModeByType(uploadMode));

        if (jsonNode != null) {

            if (jsonNode.get("errorCode").asInt() != 0) {
                throw new ServerException("电信失败：" + jsonNode.get("errorMessage").asText());
            }

            // 免审时
            if (jsonNode.has("fileInfo")) {
                JsonNode fileInfo = jsonNode.get("fileInfo");
                ArrayNode array = (ArrayNode) fileInfo;

                JsonNode result = array.get(0);
                String fileName = "";
                int fileSize = 0;
                String until = null;
                String contentType = "";
                String url = "";

                if (result.has("fileName")) {
                    fileName = result.get("fileName").asText();
                }

                if (result.has("fileSize")) {
                    fileSize = result.get("fileSize").asInt();
                }

                if (result.has("until")) {
                    until = result.get("until").asText().replace("T", " ").replace("Z", "");
                }

                if (result.has("contentType")) {
                    contentType = result.get("contentType").asText();
                }

                if (result.has("url")) {
                    url = result.get("url").asText();
                }

                materialAuditRecordMapper.saveMaterialAuditRecord("", material.getId(), Carrier.CTCC, chatbot.getChatBotId(), uploadMode, url, contentType, fileName, until, fileSize, 1);
            }

            if (jsonNode.has("fileIdList")) {
                JsonNode fileIdList = jsonNode.get("fileIdList");
                fileIdList.forEach(node -> materialAuditRecordMapper.saveMaterialAuditRecord(node.asText(), material.getId(), Carrier.CTCC, chatbot.getChatBotId(), uploadMode, "", "", "", null, 0, 0));
            }

        }
    }

}
