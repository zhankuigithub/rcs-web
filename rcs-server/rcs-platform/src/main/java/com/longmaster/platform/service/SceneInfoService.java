package com.longmaster.platform.service;

import com.longmaster.platform.dto.scene.SceneNodeModifyDTO;
import com.longmaster.platform.dto.scene.SceneQueryDTO;
import com.longmaster.platform.entity.SceneInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.core.vo.PageParam;

/**
 * author zk
 * date 2021/4/30 14:00
 */
public interface SceneInfoService  extends IService<SceneInfo> {

    /**
     * 分页查询
     * @param pageParam
     * @return
     */
    IPage<SceneInfo> pageQuery(PageParam<SceneQueryDTO> pageParam);


    /**
     * 删除场景，已经场景下的节点
     * @param id
     * @return
     */
    boolean deleteScene(Long id);

    /**
     * 启用，停用场景
     * @param id
     * @param status
     * @return
     */
    boolean openOrClose(Long id, Integer status);

    /**
     * 变更场景，新增，修改统一入口
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    boolean modifyScene(SceneNodeModifyDTO request) throws JsonProcessingException;

}
