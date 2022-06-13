package com.longmaster.platform.controller.manage;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.util.StrUtils;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.carrier.CarrierDTO;
import com.longmaster.platform.entity.Carrier;
import com.longmaster.platform.service.CarrierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 运营商信息表 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@RestController
@RequestMapping("/manage/carrier")
@Api(value = "CarrierController", tags = "运营商信息")
public class CarrierController {

    @Resource
    private CarrierService carrierService;

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "分页查询运营商信息")
    public Result pageCarrier(@RequestBody PageParam<Carrier> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        IPage<Carrier> page = carrierService.page(pageParam.getPage(), new QueryWrapper(StrUtils.rebuildEntity(pageParam.getParams())));
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @PostMapping(path = "/items")
    @ApiOperation(value = "运营商列表", notes = "查询运营商列表")
    public Result listCarrier(HttpServletRequest request) {
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        List<Carrier> items = carrierService.queryCarriers(carrierIds);
        return Result.SUCCESS(items);
    }

    @PostMapping(path = "/{appId}")
    @ApiOperation(value = "应用运营商", notes = "查询应用开通运营商列表")
    public Result<List<CarrierDTO>> getCarrierByAppId(@PathVariable Long appId, HttpServletRequest request) {
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        List<CarrierDTO> items = carrierService.queryCarrierByAppId(appId, carrierIds);
        return Result.SUCCESS(items);
    }
}

