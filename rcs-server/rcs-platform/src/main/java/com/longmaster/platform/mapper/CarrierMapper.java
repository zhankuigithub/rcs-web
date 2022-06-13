package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.carrier.CarrierDTO;
import com.longmaster.platform.entity.Carrier;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 运营商信息表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface CarrierMapper extends BaseMapper<Carrier> {

    List<Carrier> selectCarriers(@Param("carrierIds") String carrierIds);

    List<CarrierDTO> selectCarrierByAppId(@Param("appId") Long appId, @Param("carrierIds") String carrierIds);


}
