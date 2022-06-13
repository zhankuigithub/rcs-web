package com.longmaster.platform.controller.manage;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.card.CardDTO;
import com.longmaster.platform.dto.card.CardQueryDTO;
import com.longmaster.platform.dto.card.CardWrapperDto;
import com.longmaster.platform.service.CardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 卡片信息表 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@RestController
@RequestMapping("/manage/card")
@Api(value = "CardController", tags = "卡片信息")
public class CardController {

    @Resource
    private CardService cardService;

    @PostMapping
    @ApiOperation(value = "新增、修改卡片", notes = "创建、修改卡片信息")
    public Result<CardWrapperDto> createCard(@RequestBody @Validated CardWrapperDto card) {
        Assert.notNull(card, new ServerException("卡片信息不允许为空！"));
        cardService.saveCard(card);
        return Result.SUCCESS(card);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除卡片", notes = "删除卡片信息")
    public Result moveCard(@PathVariable Long id) {
        Assert.notNull(id, new ServerException("卡片ID不允许为空！"));
        cardService.deleteCard(id);
        return Result.SUCCESS(true);
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页（卡片库）", notes = "获取卡片列表-查询分页数据")
    public Result<PageResult<CardDTO>> pageQuery(@RequestBody PageParam<CardQueryDTO> pageParam, HttpServletRequest request) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        IPage<CardDTO> page = cardService.pageQuery(pageParam, carrierIds);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "卡片详情", notes = "获取卡片详细信息")
    public Result<CardWrapperDto> getCard(@PathVariable Long id) {
        CardWrapperDto cardWrapper = cardService.getCardById(id);
        return Result.SUCCESS(cardWrapper);
    }
}
