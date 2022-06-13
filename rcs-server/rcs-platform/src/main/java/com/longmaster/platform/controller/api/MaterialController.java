package com.longmaster.platform.controller.api;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.cache.CacheHelper;
import com.longmaster.platform.cache.GlobalCacheKeyDefine;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.entity.MaterialAuditRecord;
import com.longmaster.platform.service.ChatbotService;
import com.longmaster.platform.service.MaterialAuditRecordService;
import com.longmaster.platform.service.MaterialService;
import com.longmaster.platform.service.RedisService;
import com.longmaster.platform.util.UrlUtil;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController("apiMaterialController")
@RequestMapping("api/material")
@Api(value = "MaterialController", tags = "获取素材信息")
public class MaterialController {

    @Resource
    private MaterialAuditRecordService materialAuditRecordService;

    @Resource
    private ChatbotService chatbotService;

    @Resource
    private MaterialService materialService;

    @Resource
    private RedisService redisService;

    @GetMapping("getAuditMaterialUrl")
    public Result getAuditMaterialUrl(String chatBotId, Long materialId, boolean isMessageBack) {
        if (isMessageBack) {
            Material material = materialService.getOne(new LambdaQueryWrapper<Material>()
                    .eq(Material::getId, materialId).select(Material::getSourceUrl));
            if (material != null) {
                MaterialAuditRecord record = new MaterialAuditRecord(); // 保持形式，调用方不需要修改
                record.setMaterialUrl(UrlUtil.buildMinIoURL(material.getSourceUrl()));
                return Result.SUCCESS(record);
            }
            return Result.FAILED("not find");
        }
        String key = CacheHelper.buildChatBotMaterialUrlCacheKey(GlobalCacheKeyDefine.CHAT_BOT_MATERIAL_URL_ID, materialId, chatBotId);

        MaterialAuditRecord record = redisService.unblockingGet(key, 60 * 60L, () -> materialAuditRecordService.getOne(new LambdaQueryWrapper<MaterialAuditRecord>()
                .eq(MaterialAuditRecord::getChatBotId, chatBotId)
                .eq(MaterialAuditRecord::getMaterialId, materialId)
                .select(MaterialAuditRecord::getMaterialUrl)));
        if (record != null) {
            return Result.SUCCESS(record);
        }
        return Result.FAILED("not find");
    }

    @GetMapping("getAuditMaterialUrlByOriginalUrl")
    public Result getAuditMaterialUrlByOriginalUrl(String chatBotId, String originalUrl, boolean isMessageBack) {
        if (StrUtil.isEmpty(originalUrl)) {
            return Result.FAILED("originalUrl is empty");
        }
        Chatbot one = chatbotService.getOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId).select(Chatbot::getAppId));
        if (one != null) {
            Material material = materialService.getOne(new LambdaQueryWrapper<Material>().eq(Material::getAppId, one.getAppId()).eq(Material::getOriginalUrl, originalUrl).select(Material::getId));
            if (material != null) {
                return getAuditMaterialUrl(chatBotId, material.getId(), isMessageBack);
            }
        }
        return Result.FAILED("not find");
    }

}
