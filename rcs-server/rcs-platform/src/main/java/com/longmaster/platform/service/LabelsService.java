package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.label.LabelQueryDTO;
import com.longmaster.platform.dto.phoneBook.BatchUpdLabelDTO;
import com.longmaster.platform.entity.Labels;

import java.util.List;
import java.util.Map;

public interface LabelsService extends IService<Labels> {

    boolean addLabel(Labels label);

    boolean updLabel(Labels label);

    boolean deleteLabel(Long id);


    IPage<Labels> pageQuery(PageParam<LabelQueryDTO> pageParam);


    List<Map<String, Object>> group(Long id);


    boolean batchUpdLabel(BatchUpdLabelDTO batchUpdLabel);
}
