package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longmaster.platform.entity.Carrier;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.entity.MaterialAuditRecord;
import com.longmaster.platform.mapper.ChatbotMapper;
import com.longmaster.platform.mapper.MaterialAuditRecordMapper;
import com.longmaster.platform.service.RcsService;
import com.longmaster.platform.util.UrlUtil;
import com.longmaster.core.exception.ServerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class CmUploadStrategy extends AbstractUploadStrategy {
    @Resource
    private MaterialAuditRecordMapper materialAuditRecordMapper;

    @Resource
    private ChatbotMapper chatbotMapper;

    @Resource
    private RcsService rcsService;

    @Override
    public Long getCarrierId() {
        return Carrier.CMCC;
    }

    @Override
    public void upload(Material material, int uploadMode) throws IOException {
        Long appId = material.getAppId();  // 获取移动下的该应用下的chatbot
        Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getAppId, appId).eq(Chatbot::getCarrierId, Carrier.CMCC).select(Chatbot::getChatBotId));

        if (chatbot == null) {
            throw new ServerException("中国移动失败：此应用下未创建chatbot");
        }

        long count = materialAuditRecordMapper.
                selectCount(new LambdaQueryWrapper<MaterialAuditRecord>().
                        eq(MaterialAuditRecord::getChatBotId, chatbot.getChatBotId()).
                        eq(MaterialAuditRecord::getMaterialId, material.getId()));

        if (count > 0) {
            throw new ServerException("中国移动失败：此素材已经提交过审核了");
        }

        String tid = rcsService.cmUploadChatBotMaterial(chatbot.getChatBotId(), UrlUtil.buildMinIoURL(material.getSourceUrl()));

        if (StrUtil.isEmpty(tid)) {
            throw new ServerException("中国移动失败：tid为空");
        }

        materialAuditRecordMapper.saveMaterialAuditRecord(tid,
                material.getId(), Carrier.CMCC,
                chatbot.getChatBotId(), uploadMode, "", "", "", null, 0, 0);
    }

}
