package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.notice.NoticeQueryDTO;
import com.longmaster.platform.entity.Notice;

import java.util.List;

public interface NoticeService  extends IService<Notice> {

    IPage<Notice> pageQuery(PageParam<NoticeQueryDTO> pageParam);

    List<Notice> queryList(Long customerId);
}
