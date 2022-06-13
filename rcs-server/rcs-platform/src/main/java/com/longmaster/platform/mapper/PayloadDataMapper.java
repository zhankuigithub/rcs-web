package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longmaster.platform.entity.PayloadData;
import org.apache.ibatis.annotations.Param;

public interface PayloadDataMapper extends BaseMapper<PayloadData> {

    void deleteByIds(@Param("ids") String ids);


    void deleteBySugIds(@Param("sugIds") String sugIds);


    void deleteByMenuIds(@Param("menuIds") String menuIds);

}
