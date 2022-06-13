package com.longmaster.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.platform.dto.event.PushEventDTO;
import com.longmaster.platform.entity.PushEvent;

/**
 * author zk
 * date 2021/3/2 16:48
 */
public interface PushEventService extends IService<PushEvent> {

    /**
     * 查询任务列表
     * @param pageParam
     * @return
     */
    PageResult<PushEventDTO> pageQuery(PageParam<PushEvent> pageParam);

    /**
     * 激活或暂停
     * @param taskId
     * @return
     */
    boolean activateOrSuspend(String taskId);

    /**
     * 取消任务
     * @param taskId
     * @return
     */
    boolean cancel(String taskId);

}
