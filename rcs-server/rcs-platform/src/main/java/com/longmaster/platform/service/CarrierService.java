package com.longmaster.platform.service;

import com.longmaster.platform.dto.carrier.CarrierDTO;
import com.longmaster.platform.entity.Carrier;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 运营商信息表 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface CarrierService extends IService<Carrier> {

    List<Carrier> queryCarriers(String carrierIds);

    List<CarrierDTO> queryCarrierByAppId(Long appId, String carrierIds);
}
