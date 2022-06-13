package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.material.MaterialDTO;
import com.longmaster.platform.dto.material.MaterialQueryDTO;
import com.longmaster.platform.entity.Material;

import java.io.IOException;

/**
 * <p>
 * 素材信息表 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface MaterialService extends IService<Material> {

    /**
     * 分也查询素材相关信息
     *
     * @param pageParam 分页参数
     * @return 分页数据
     */
    IPage<MaterialDTO> pageQuery(PageParam<MaterialQueryDTO> pageParam, String carrierIds);


    Long saveMaterial(Long appId, Long customerId, String fileName, Integer fileType,
                      String url, String originalUrl, Long thumbId, Integer isThumb, Integer status, Integer attribution, String content);


    ArrayNode getMaterialAuditStatus(Long materialId, String carrierIds);




    /**
     * 删除素材
     *
     * @param id
     * @return
     */
    Result deleteMaterial(Long id);

    /**
     * 素材详情
     *
     * @param id
     * @return
     */
    MaterialDTO item(String id);


    /**
     *
     * @param sourceUrl
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    String createThumbnail(String sourceUrl) throws IOException, InterruptedException;

}
