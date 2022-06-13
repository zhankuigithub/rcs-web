package com.longmaster.platform.controller.manage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.util.ExcelUtil;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.label.LabelIdsDTO;
import com.longmaster.platform.dto.phoneBook.*;
import com.longmaster.platform.service.PhoneNumberBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/manage/phoneNumberBook")
@Api(value = "PhoneNumberBookController", tags = "通讯录")
public class PhoneNumberBookController {

    @Resource
    private PhoneNumberBookService phoneNumberBookService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @PostMapping("/add")
    @ApiOperation(value = "新增联系人", notes = "新增联系人")
    public Result addPhoneNumberBook(@RequestBody @Validated PhoneNumberModifyDTO phoneNumberBook) {
        Assert.notNull(phoneNumberBook, new ServiceException("参数不允许为空"));
        return Result.SUCCESS(phoneNumberBookService.addPhone(phoneNumberBook));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除联系人", notes = "删除联系人")
    public Result movePhoneNumberBook(@PathVariable Long id) {
        Assert.notNull(id, new ServerException("ID不允许为空！"));
        return Result.SUCCESS(phoneNumberBookService.deletePhone(id));
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改联系人", notes = "修改联系人")
    public Result editPhoneNumberBook(@RequestBody @Validated(PhoneNumberModifyDTO.Update.class) PhoneNumberModifyDTO phoneNumberBook) {
        Assert.notNull(phoneNumberBook, new ServerException("联系人信息不允许为空！"));
        return Result.SUCCESS(phoneNumberBookService.updPhone(phoneNumberBook));
    }

    @PostMapping("/page")
    @ApiOperation(value = "联系人列表", notes = "查询联系人列表")
    public Result<PageResult<PhoneNumberDetailDTO>> pageQuery(@RequestParam(required = false, defaultValue = "false") boolean isExcel, HttpServletResponse response, @RequestBody PageParam<PhoneNumberBookQueryDTO> pageParam) throws IOException {
        if (isExcel) {
            pageParam.setCurrentPage(1);
            pageParam.setPageSize(100000);
            PageResult<PhoneNumberDetailDTO> query = phoneNumberBookService.pageQuery(pageParam);
            ExcelUtil.downloadExcel(response, sObjectMapper.readTree(sObjectMapper.writeValueAsString(query)),
                    Arrays.asList("姓名", "手机号", "性别", "标签", "禁用的应用名单", "备注"),
                    Arrays.asList("name", "phoneNum", "sexTag", "labelNames", "appBlacklistNames", "remark"));
            return null;
        }
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        return Result.SUCCESS(phoneNumberBookService.pageQuery(pageParam));
    }

    @PostMapping("pageByCid")
    @ApiOperation(value = "按照客户id分组查询", notes = "按照客户id分组查询")
    public Result<PageResult<PhoneNumberDTO>> selectPhoneNumberBook(@RequestBody PageParam<Map<String, Object>> pageParam) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        return Result.SUCCESS(phoneNumberBookService.selectPhoneNumberBook(pageParam));
    }

    @DeleteMapping("/deleteByCid/{customerId}")
    @ApiOperation(value = "按照客户id删除联系人", notes = "按照客户id删除联系人")
    public Result deleteByCid(@PathVariable String customerId) {
        return Result.SUCCESS(phoneNumberBookService.deleteByCid(Long.parseLong(customerId)));
    }

    @PostMapping("getPhonesByLabel")
    @ApiOperation(value = "通过标签id获取手机号", notes = "通过标签id获取手机号")
    public Result getPhonesByLabel(@RequestBody LabelIdsDTO labelIds) {
        return Result.SUCCESS(phoneNumberBookService.getPhonesByLabel(labelIds.getIds()));
    }

    @PostMapping("batchAdd")
    @ApiOperation(value = "批量加号码", notes = "批量加号码")
    public Result batchAdd(@RequestBody BatchAddPhDTO batchAddPh) {
        return getOperateResult(phoneNumberBookService.batchAdd(batchAddPh));
    }

    @PostMapping(value = "batchAddByExcel", consumes = {"multipart/form-data"})
    @ApiOperation(value = "通过excel批量加号码", notes = "通过excel批量加号码")
    public Result batchAddByExcel(@RequestParam Long customerId, @RequestParam String remark, @RequestParam MultipartFile file,
                                  @RequestParam(required = false) List<Long> labelIds, @RequestParam(required = false) List<Long> applicationIds) throws IOException {
        BatchAddExcelPhDTO batchAddExcelPh = new BatchAddExcelPhDTO();
        batchAddExcelPh.setCustomerId(customerId);
        batchAddExcelPh.setFile(file);
        batchAddExcelPh.setLabelIds(labelIds);
        batchAddExcelPh.setApplicationIds(applicationIds);
        batchAddExcelPh.setRemark(remark);
        return getOperateResult(phoneNumberBookService.batchAddByExcel(batchAddExcelPh));
    }

    @PostMapping("readExcel")
    @ApiOperation(value = "读取excel的号码", notes = "读取excel的号码")
    public Result readExcel(@RequestParam MultipartFile file) throws IOException {
        return Result.SUCCESS(phoneNumberBookService.readExcel(file));
    }

    public static Result getOperateResult(List<String> failedIds) {
        if (!failedIds.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("号码：");
            builder.append(failedIds);
            builder.append("已经存在~");
            return Result.SUCCESS(builder.toString());
        } else {
            return Result.SUCCESS();
        }
    }

}
