package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longmaster.core.http.HttpConfig;
import com.longmaster.core.http.OkHttpRequestImpl;
import com.longmaster.core.threadpool.AsyncTaskThreadManager;
import com.longmaster.core.util.MediaFileUtil;
import com.longmaster.platform.entity.Carrier;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.entity.MaterialAuditRecord;
import com.longmaster.platform.enums.UploadPathEnum;
import com.longmaster.platform.mapper.MaterialAuditRecordMapper;
import com.longmaster.platform.mapper.MaterialMapper;
import com.longmaster.platform.service.MinioService;
import com.longmaster.platform.service.RcsService;
import com.longmaster.platform.service.UploadService;
import com.longmaster.platform.service.UploadStrategy;
import com.longmaster.core.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author zk
 * date 2021/2/25 13:46
 */
@Slf4j
@Component
public class UploadServiceImpl implements UploadService {

    @Resource
    private MaterialAuditRecordMapper materialAuditRecordMapper;

    @Resource
    private MaterialMapper materialMapper;

    @Resource
    private RcsService rcsService;

    @Resource
    private List<UploadStrategy> strategies;

    @Resource
    private MinioService minioService;

    private final OkHttpRequestImpl okHttpRequest = new OkHttpRequestImpl(HttpConfig.defaultConfig());


    @Override
    public Map<Long, String> submit(List<Long> ids, List<Long> carrierIds, int uploadMode) throws IOException {
        Map<Long, String> failedIds = new HashMap<>();
        // 查出所有的文件
        List<Material> materials = materialMapper.selectMaterialByIdStr(StrUtil.join(",", ids));
        for (Material material : materials) {
            for (UploadStrategy strategy : strategies) {
                if (carrierIds.contains(strategy.getCarrierId())) {
                    try {
                        strategy.upload(material, uploadMode);
                    } catch (ServerException e) {
                        if (failedIds.containsKey(material.getId())) {
                            failedIds.put(material.getId(), failedIds.get(material.getId()) + "|" + e.getMessage());
                        } else {
                            failedIds.put(material.getId(), e.getMessage());
                        }
                    }
                }
            }
        }
        return failedIds;
    }

    @Override
    public void deleteMaterial(Long id) {
        AsyncTaskThreadManager.getInstance().submit(() -> { // 太耗时，加到异步里面
            Material material = materialMapper.selectOne(new LambdaQueryWrapper<Material>().eq(Material::getId, id).select(Material::getSourceUrl));
            if (material != null) {
                String sourceUrl = material.getSourceUrl();
                minioService.remove(sourceUrl);
            }

            // 查出素材信息
            List<MaterialAuditRecord> materialAuditRecords = materialAuditRecordMapper.
                    selectList(new LambdaQueryWrapper<MaterialAuditRecord>().eq(MaterialAuditRecord::getMaterialId, id));

            materialAuditRecords.forEach(item -> {
                Long carrierId = item.getCarrierId();

                if (carrierId.longValue() == Carrier.CMCC.longValue()) {
                    rcsService.cmDeleteChatBotMaterial(item.getChatBotId(), item.getTid());
                } else if (carrierId.longValue() == Carrier.CUCC.longValue()) {
                    rcsService.cuDeleteChatBotMaterial(item.getChatBotId(), item.getMaterialUrl());
                } else if (carrierId.longValue() == Carrier.CTCC.longValue()) {
                    rcsService.ctDeleteChatBotMaterial(item.getChatBotId(), item.getMaterialUrl());
                } else if (carrierId.longValue() == Carrier.GUANG_XI_CMCC.longValue()) {
                    rcsService.gxCmDeleteChatBotMaterial(item.getChatBotId(), item.getTid());
                }
            });
        });
    }

    @Override
    public String upload(String originalUrl) {
        byte[] bytes;
        try {
            bytes = okHttpRequest.GET(originalUrl, null, null).body().bytes();
            String mimeType = "";
            try {
                mimeType = MediaFileUtil.getFileType(originalUrl).getMimeType();
            } catch (Exception e) {
                mimeType = "application/octet-stream";
            }
            String fileName = originalUrl.substring(originalUrl.lastIndexOf('/') + 1);
            return minioService.uploadFile(bytes, fileName, new String[]{".jpg", ".png", ".mp3", ".mp4", ".jpeg", ".amr"}, mimeType, UploadPathEnum.MATERIAL);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<Long, String> rebuildMaterial(Long materialId, Long carrierId) {
        MaterialAuditRecord record = materialAuditRecordMapper.selectOne(new LambdaQueryWrapper<MaterialAuditRecord>().eq(MaterialAuditRecord::getMaterialId, materialId)
                .eq(MaterialAuditRecord::getCarrierId, carrierId));

        if (carrierId.longValue() == Carrier.CMCC.longValue()) {
            rcsService.cmDeleteChatBotMaterial(record.getChatBotId(), record.getTid());
        } else if (carrierId.longValue() == Carrier.CUCC.longValue()) {
            rcsService.cuDeleteChatBotMaterial(record.getChatBotId(), record.getMaterialUrl());
        } else if (carrierId.longValue() == Carrier.CTCC.longValue()) {
            rcsService.ctDeleteChatBotMaterial(record.getChatBotId(), record.getMaterialUrl());
        } else if (carrierId.longValue() == Carrier.GUANG_XI_CMCC.longValue()) {
            rcsService.gxCmDeleteChatBotMaterial(record.getChatBotId(), record.getTid());
        }

        // 删除旧记录
        materialAuditRecordMapper.deleteRecord(record.getId());

        // 重新提交
        try {
            return this.submit(Arrays.asList(materialId), Arrays.asList(carrierId), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
