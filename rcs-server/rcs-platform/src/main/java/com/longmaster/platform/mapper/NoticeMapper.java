package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.notice.NoticeQueryDTO;
import com.longmaster.platform.entity.Notice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoticeMapper extends BaseMapper<Notice> {

    IPage<Notice> pageSelect(IPage<NoticeQueryDTO> page, @Param("ew") NoticeQueryDTO params);

    List<Notice> queryList(String customerId);
}
