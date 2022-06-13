package com.longmaster.platform.service;

import java.util.List;
import java.util.Map;

/**
 * author zk
 * date 2021/2/25 13:46
 * description 上传service
 */
public interface UploadService {

    // 提交运营商审核
    Map<Long, String> submit(List<Long> ids, List<Long> carrierIds, int uploadMode) throws Exception;


    // 删除素材
    void deleteMaterial(Long id);


    // csp后台填写原图url，上传至minio
    String upload(String originalUrl);


    // 重新构建运营商素材
    Map<Long, String> rebuildMaterial(Long materialId, Long carrierId);
}
