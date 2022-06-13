package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.scene.SceneQueryDTO;
import com.longmaster.platform.entity.SceneInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

/**
 * author zk
 * date 2021/4/30 13:57
 */
public interface SceneInfoMapper extends BaseMapper<SceneInfo> {

    IPage<SceneInfo> pageSelect(IPage<SceneQueryDTO> page, @Param("ew") SceneQueryDTO params);


    int updateScene(@Param("id") Long id, @Param("status") int status);

}
