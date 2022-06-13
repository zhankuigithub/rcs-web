package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.core.util.MediaFileUtil;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.MenuAuditRecord;
import com.longmaster.platform.mapper.ChatbotMapper;
import com.longmaster.platform.mapper.MaterialAuditRecordMapper;
import com.longmaster.platform.mapper.MenuAuditRecordMapper;
import com.longmaster.platform.service.AuditService;
import com.longmaster.platform.service.RcsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * author zk
 * date 2021/2/27 9:19
 */
@Component
public class AuditServiceImpl implements AuditService {

    @Resource
    private MaterialAuditRecordMapper materialAuditRecordMapper;

    @Resource
    private RcsService rcsService;

    @Resource
    private MenuAuditRecordMapper menuAuditRecordMapper;

    @Resource
    private ChatbotMapper chatbotMapper;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();


    @Override
    public int auditCMCC(JsonNode jsonNode) {

        String type = jsonNode.get("type").asText();

        // 媒体审核
        if ("media".equals(type)) {
            if (jsonNode.has("tid") && jsonNode.has("file")) {
                String tid = jsonNode.get("tid").asText();

                JsonNode file = jsonNode.get("file");

                if (file.has("file-info")) {

                    JsonNode fileInfo = file.get("file-info");
                    JsonNode data = fileInfo.get("data");
                    String until = data.get("until").asText().replace("T", " ").replace("Z", "");
                    String url = data.get("url").asText();

                    int fileSize = fileInfo.get("file-size").asInt();
                    String fileName = fileInfo.get("file-name").asText();
                    String contentType = "";

                    try {
                        if (fileName.contains("?")) {
                            fileName = fileName.substring(0, fileName.indexOf("?"));
                        }
                        contentType = MediaFileUtil.getFileType(fileName).getMimeType();   // 使用file name
                    } catch (Exception ex) {
                        contentType = fileInfo.get("content-type").asText(); // 传来的全是application/octet-stream 手机上无法识别，先使用url获取文件类型，获取不到再默认application/octet-stream
                    }


                    // 通过tid修改
                    materialAuditRecordMapper.updateMaterialAuditRecordByTid(url, contentType, fileName, until, fileSize, tid);
                }
            }
        }
        return 0;
    }

    @Override
    public int auditCTCC(JsonNode jsonNode) throws JsonProcessingException {

        String type = jsonNode.get("type").asText();

        if ("media".equals(type)) {
            String remark = jsonNode.get("remark").asText();
            String[] split = remark.split(":");
            String tid = split[1].trim();

            JsonNode fileInfo = jsonNode.get("fileInfo");
            String url = fileInfo.get("url").asText();
            String fileName = fileInfo.get("fileName").asText();
            String contentType = fileInfo.get("contentType").asText();
            int fileSize = fileInfo.get("fileSize").asInt();
            String until = fileInfo.get("until").asText();
            if (StrUtil.isEmpty(until) || "null".equals(until)) { // 局方可能传来"null"字符串
                until = null;
            } else {
                until = fileInfo.get("until").asText().replace("T", " ").replace("Z", "");
            }
            materialAuditRecordMapper.updateMaterialAuditRecordByTid(url, contentType, fileName, until, fileSize, tid);
        }

        // 信息变更
        if ("chatbotInformation".equals(type)) {
            String description = jsonNode.has("description") ? jsonNode.get("description").asText() : (jsonNode.has("result") ? jsonNode.get("result").asText() : "");
            String chatBotId = jsonNode.get("chatBotId").asText();
            JsonNode information = rcsService.ctChatBotInfo(chatBotId);
            String menu = sObjectMapper.writeValueAsString(information.get("menu"));

            // 查出主键
            Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId));

            Long id = chatbot.getId();
            MenuAuditRecord menuAuditRecord = menuAuditRecordMapper.selectLastMenuAuditRecord(id);

            if (menuAuditRecord == null) {
                return -1;
            }
            menuAuditRecordMapper.updateMenuAuditRecord(menuAuditRecord.getId(), menu, description);
        }
        return 0;
    }

    @Override
    public int auditCUCC(JsonNode jsonNode) throws JsonProcessingException {
        String type = jsonNode.get("type").asText();

        if ("media".equals(type)) {
            String remark = jsonNode.get("remark").asText();
            String[] split = remark.split(":");
            String tid = split[1].trim();

            JsonNode fileInfo = jsonNode.get("fileInfo");
            String url = fileInfo.get("url").asText();
            String fileName = fileInfo.get("fileName").asText();
            String contentType = fileInfo.get("contentType").asText();
            int fileSize = fileInfo.get("fileSize").asInt();
            String until = fileInfo.get("until").asText();
            if (StrUtil.isEmpty(until) || "null".equals(until)) { // 局方可能传来"null"字符串
                until = null;
            } else {
                until = fileInfo.get("until").asText().replace("T", " ").replace("Z", "");
            }
            materialAuditRecordMapper.updateMaterialAuditRecordByTid(url, contentType, fileName, until, fileSize, tid);
        }

        // 信息变更
        if ("chatbotInformation".equals(type)) {
            String description = jsonNode.has("description") ? jsonNode.get("description").asText() : (jsonNode.has("result") ? jsonNode.get("result").asText() : "");
            String chatBotId = jsonNode.get("chatBotId").asText();
            JsonNode information = rcsService.cuChatBotInfo(chatBotId);
            String menu = sObjectMapper.writeValueAsString(information.get("menu"));

            // 查出主键
            Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId));

            Long id = chatbot.getId();
            MenuAuditRecord menuAuditRecord = menuAuditRecordMapper.selectLastMenuAuditRecord(id);

            if (menuAuditRecord == null) {
                return -1;
            }
            menuAuditRecordMapper.updateMenuAuditRecord(menuAuditRecord.getId(), menu, description);
        }
        return 0;

    }

}
