package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.label.LabelQueryDTO;
import com.longmaster.platform.entity.Labels;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LabelsMapper extends BaseMapper<Labels> {


    IPage<Labels> pageSelect(IPage<LabelQueryDTO> page, @Param("ew") LabelQueryDTO params);

    List<Map<String, Object>> selectGroup(@Param("customerId") Long customerId);
}
