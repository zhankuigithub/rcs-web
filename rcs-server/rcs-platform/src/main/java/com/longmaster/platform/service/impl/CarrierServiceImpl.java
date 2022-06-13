package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.platform.dto.carrier.CarrierDTO;
import com.longmaster.platform.entity.Carrier;
import com.longmaster.platform.mapper.CarrierMapper;
import com.longmaster.platform.service.CarrierService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 运营商信息表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Service
public class CarrierServiceImpl extends ServiceImpl<CarrierMapper, Carrier> implements CarrierService {


    @Override
    public List<Carrier> queryCarriers(String carrierIds) {
        if (carrierIds == null) {
            return null;
        }
        return baseMapper.selectCarriers(carrierIds);
    }

    @Override
    public List<CarrierDTO> queryCarrierByAppId(Long appId, String carrierIds) {
        if (carrierIds == null) {
            return null;
        }
        return baseMapper.selectCarrierByAppId(appId, carrierIds);
    }
}
