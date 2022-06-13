package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.material.MaterialDTO;
import com.longmaster.platform.dto.material.MaterialQueryDTO;
import com.longmaster.platform.entity.Material;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 素材信息表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface MaterialMapper extends BaseMapper<Material> {

    List<Material> selectMaterialByIdStr(@Param("idStr") String idStr);

    IPage<MaterialDTO> pageSelect(IPage<MaterialDTO> page, @Param("ew") MaterialQueryDTO params);

    Long selectOneMaterial(@Param("originalUrl") String originalUrl, boolean isThumb);

    MaterialDTO item(Long id);
}
