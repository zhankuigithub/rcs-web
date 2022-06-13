package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longmaster.core.http.HttpConfig;
import com.longmaster.core.http.IHttpRequest;
import com.longmaster.core.http.OkHttpRequestImpl;
import com.longmaster.core.util.MediaFileUtil;
import com.longmaster.platform.entity.Carrier;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.mapper.ChatbotMapper;
import com.longmaster.platform.mapper.MaterialAuditRecordMapper;
import com.longmaster.platform.util.UrlUtil;
import com.longmaster.core.exception.ServerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class LmUploadStrategy extends AbstractUploadStrategy {

    @Resource
    private MaterialAuditRecordMapper materialAuditRecordMapper;

    @Resource
    private ChatbotMapper chatbotMapper;

    private IHttpRequest okHttpRequest = new OkHttpRequestImpl(HttpConfig.defaultConfig());

    @Override
    public Long getCarrierId() {
        return Carrier.LONGMASTER;
    }

    @Override
    public void upload(Material material, int uploadMode) throws IOException {

        Long appId = material.getAppId();
        Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getAppId, appId)
                .eq(Chatbot::getCarrierId, Carrier.LONGMASTER)
                .eq(Chatbot::getStatus, 1)
                .eq(Chatbot::getAuditStatus, 1)
                .select(Chatbot::getChatBotId));

        if (chatbot == null) {
            throw new ServerException("LONGMASTER失败：此应用下没有正在启用的chatbot");
        }
        String sourceUrl = UrlUtil.buildMinIoURL(material.getSourceUrl());
        String contentType = "";
        try {
            String url = sourceUrl;
            if (url.contains("?key=")) {
                url = url.substring(0, url.indexOf("?key="));
            }
            contentType = MediaFileUtil.getFileType(url).getMimeType();
        } catch (Exception e) {
            contentType = "application/octet-stream";
        }

        int fileSize;
        try {
            fileSize = okHttpRequest.GET(sourceUrl, null, null).body().bytes().length;
        } catch (Exception e) {
            fileSize = 1024;
        }

        materialAuditRecordMapper.saveMaterialAuditRecord(
                "",
                material.getId(),
                Carrier.LONGMASTER,
                chatbot.getChatBotId(),
                uploadMode,
                sourceUrl,
                contentType,
                material.getName(),
                null,
                fileSize,
                1);
    }
}
