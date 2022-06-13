package com.longmaster.platform.controller.manage;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.core.base.SuperEntity;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.customer.*;
import com.longmaster.platform.entity.Customer;
import com.longmaster.platform.enums.UploadPathEnum;
import com.longmaster.platform.service.CustomerService;
import com.longmaster.platform.service.MinioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 客户信息表 前端控制器
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@RestController
@RequestMapping("/manage/customer")
@Api(value = "CustomerController", tags = "客户信息")
public class CustomerController {

    @Resource
    private CustomerService customerService;

    @Resource
    private MinioService minioService;

    private static final int AUDIT_PASS_STATUS = 1;


    @GetMapping
    @ApiOperation(value = "客户列表", notes = "下拉客户列表展示数据")
    public Result<List<Customer>> listOfCustomer(boolean isAll) {
        List<Customer> list = customerService.list(new LambdaQueryWrapper<Customer>()
                .select(Customer::getId, Customer::getName)
                .eq(!isAll, Customer::getStatus, AUDIT_PASS_STATUS)
        );
        return Result.SUCCESS(list);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "客户基本信息", notes = "获取客户认证信息（基础信息）")
    public Result<CustomerAuthDTO> getCustomerWrapper(@PathVariable Long id) {
        Assert.notNull(id, new ServerException("分页参数不允许为空！"));
        CustomerAuthDTO authWrapper = customerService.getCustomerWrapper(id);
        return Result.SUCCESS(authWrapper);
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "分页查询客户信息")
    public Result<PageResult<CustomerDTO>> pageCustomer(@RequestBody PageParam<CustomerDTO> pageParam, HttpServletRequest request) {
        Assert.notNull(pageParam, new ServerException("分页参数不允许为空！"));
        String carrierIds = request.getHeader(AuthConstant.CARRIER_KEY);
        IPage<CustomerDTO> page = customerService.pageQuery(pageParam, carrierIds);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @PostMapping
    @ApiOperation(value = "创建客户", notes = "创建客户、联系人、法人等信息")
    public Result<Boolean> createCustomer(@RequestBody @Validated(SuperEntity.Create.class) CustomerAuthDTO authWrapper) {
        Assert.notNull(authWrapper, new ServerException("参数不允许为空！"));
        customerService.addCustomer(authWrapper);
        return Result.SUCCESS(true);
    }

    @PutMapping
    @ApiOperation(value = "编辑客户", notes = "编辑客户、联系人、法人等信息")
    public Result<Boolean> editCustomer(@RequestBody @Validated CustomerAuthDTO authWrapper) {
        Assert.notNull(authWrapper, new ServerException("参数不允许为空！"));
        customerService.modifyCustomer(authWrapper);
        return Result.SUCCESS(true);
    }

    @PutMapping("/audit/first")
    @ApiOperation(value = "审核客户(一审)", notes = "一审审核客户信息")
    public Result<Boolean> firstAuditCustomer(@RequestBody Customer customer) {
        Assert.notNull(customer, new ServerException("参数不允许为空！"));
        customerService.firstAuditCustomer(customer);
        return Result.SUCCESS(true);
    }

    @PostMapping("/uploadFile")
    @ApiOperation(value = "上传客户资料", notes = "上传客户资料")
    public Result<Boolean> uploadAuthMaterial(MultipartFile file) {
        Assert.notNull(file, new ServerException("请选择文件！"));
        String fileUrl = minioService.uploadFile(file, new String[]{".jpg", ".png"}, UploadPathEnum.CUSTOMER);
        return Result.SUCCESS("上传成功！", fileUrl);
    }

    @PostMapping("/submit/{carrierId}")
    @ApiOperation(value = "提交审核", notes = "保存客户信息并提交审核")
    public Result<Boolean> submit(@RequestBody CustomerCtAuthDTO wrapper, @PathVariable Long carrierId) throws Exception {
        Assert.notNull(wrapper, new ServerException("参数不允许为空"));
        customerService.submit(wrapper, carrierId);
        return Result.SUCCESS(true);
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除", notes = "删除客户信息")
    public Result<Boolean> rmCustomer(@PathVariable("id") Long customerId) throws Exception {
        Assert.notNull(customerId, new ServerException("参数customerId不允许为空"));
        return Result.SUCCESS(customerService.deleteCustomer(customerId));
    }


    @PostMapping("createCustomer")
    @ApiOperation(value = "创建客户（开放）", notes = "开放平台使用的创建客户、联系人等信息")
    public Result createCustomer(@RequestBody CertificationAddDTO certification) {
        return customerService.createCustomer(certification);
    }

    @PutMapping("syncCspCustomerMessage")
    @ApiOperation(value = "同步信息（开放）", notes = "开放平台使用的创建客户、联系人等信息")
    public Result syncCspCustomerMessage(@RequestBody CompanyDTO company) {
        return Result.SUCCESS(customerService.syncCspCustomerMessage(company));
    }

}


