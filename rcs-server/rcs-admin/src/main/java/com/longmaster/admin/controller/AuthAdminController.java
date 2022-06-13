package com.longmaster.admin.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.admin.dto.auth.AdminModifyDTO;
import com.longmaster.admin.dto.auth.AuthAdminDto;
import com.longmaster.admin.dto.auth.AuthAdminQueryDto;
import com.longmaster.admin.enums.UploadPathEnum;
import com.longmaster.admin.service.AuthAdminService;
import com.longmaster.admin.service.MinioService;
import com.longmaster.core.base.SuperEntity;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@RestController
@RequestMapping("/manage/admin")
@Api(value = "AuthAdminController", tags = "管理员")
public class AuthAdminController {

    @Resource
    private AuthAdminService adminService;

    @Resource
    private MinioService minioService;

    @PostMapping("/page")
    @ApiOperation(value = "分页", notes = "分页")
    public Result page(@RequestBody PageParam<AuthAdminQueryDto> pageParam) {
        IPage<AuthAdminDto> page = adminService.pageQuery(pageParam);
        return Result.SUCCESS(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @PostMapping("/create")
    @ApiOperation(value = "创建管理员", tags = "创建管理员")
    public Result<Boolean> addAdmin(@RequestBody @Validated(SuperEntity.Create.class) AuthAdminDto adminDto) {
        return Result.SUCCESS(adminService.createAdmin(adminDto));
    }


    @PutMapping("/modify")
    @ApiOperation(value = "更新管理员", tags = "更新管理员")
    public Result<Boolean> modifyAdmin(@RequestBody @Validated(SuperEntity.Update.class) AuthAdminDto adminDto) {
        return Result.SUCCESS(adminService.editAdmin(adminDto));
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "获取管理员信息", tags = "获取管理员信息")
    public Result getAdminInfo(@PathVariable String id) {
        return Result.SUCCESS(adminService.getAdminDetail(id));
    }

    @DeleteMapping
    @ApiOperation(value = "删除管理员", tags = "删除管理员")
    public Result delAdmin(String id) {
        Assert.isTrue(StrUtil.isNotBlank(id), new ServiceException("管理员编号不允许为空~"));
        return Result.SUCCESS(adminService.removeById(id));
    }


    @PostMapping("/uploadFile")
    @ApiOperation(value = "上传头像", notes = "上传上传头像")
    public Result<Boolean> uploadAdminPhoto(MultipartFile file) {
        Assert.notNull(file, new ServerException("请选择文件！"));
        String fileUrl = minioService.uploadFile(file, new String[]{".jpg", ".png"}, UploadPathEnum.ADMIN);
        return Result.SUCCESS("上传成功！", fileUrl);
    }

    @ApiOperation(value = "修改密码")
    @PutMapping("updatePassWord")
    public Result updatePassWord(@RequestBody AdminModifyDTO userAccountModify, @RequestHeader(AuthConstant.TOKEN_KEY) String accessToken) {
        return Result.SUCCESS(adminService.updatePassWord(userAccountModify, accessToken));
    }

}

