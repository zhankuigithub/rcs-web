package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.event.PushEventDTO;
import com.longmaster.platform.entity.PushEvent;
import org.apache.ibatis.annotations.Param;

/**
 * author zk
 * date 2021/3/2 16:47
 */
public interface PushEventMapper extends BaseMapper<PushEvent> {

    IPage<PushEventDTO> pageSelect(IPage<PushEvent> page, @Param("params") PushEvent params);

}
