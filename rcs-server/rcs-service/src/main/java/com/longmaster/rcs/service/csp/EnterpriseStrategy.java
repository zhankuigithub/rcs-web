package com.longmaster.rcs.service.csp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.rcs.dto.csp.EnterpriseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface EnterpriseStrategy {

    Long carrierId();

    /**
     * 上传客户资料
     *
     * @param file       客户资料
     * @param uploadType 文件类型
     *                   0：客户图片
     *                   1：身份证正反面
     *                   2：合同
     * @return 资料 url
     */
    JsonNode invokeUploadFile(MultipartFile file, Integer uploadType);

    /**
     * 查看客户资料
     *
     * @param fileUrl 上传返回的资料url
     * @return 文件流
     */
    byte[] invokeGetFile(String fileUrl);

    /**
     * 新增客户
     *
     * @return 客户ID
     */
    JsonNode invokeCreateCustomer(EnterpriseDTO wrapper) throws JsonProcessingException;

    /**
     * 查看客户资料
     *
     * @param
     * @return 客户信息
     */
    JsonNode invokeSelectCspCustomer(String cspEcNo);

    /**
     * 申请删除客户信息
     *
     * @param
     * @return
     */
    JsonNode invokeDeleteCustomer(String cspEcNo);

    /**
     * 变更客户信息
     *
     * @param wrapper
     * @return
     */

    JsonNode invokeUpdateCustomer(EnterpriseDTO wrapper) throws JsonProcessingException;

}
