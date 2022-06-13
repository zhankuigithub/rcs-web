package com.longmaster.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.admin.dto.auth.*;
import com.longmaster.admin.entity.AuthAdmin;
import com.longmaster.core.bean.Token;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.Result;


public interface AuthAdminService extends IService<AuthAdmin> {

    /**
     * 密码登录
     * @param account
     * @return
     */
    Token login(AuthDto account);

    /**
     * 短信验证码登录
     * @param authData
     * @return
     */
    Token loginBySmsCode(AuthSmsDto authData);

    /**
     * 账户是否存在
     * @param account
     * @return
     */
    boolean existAdmin(String account);

    /**
     * 登出
     * @param accessToken
     * @return
     */
    boolean logout(String accessToken);

    /**
     * 获取当前登录人的基本信息
     * @param accessToken
     * @return
     */
    AuthAdminDto getInfo(String accessToken);

    /**
     * 注册
     * @param adminAccount
     * @return
     */
    AuthAdmin register(AuthAdminDto adminAccount);

    /**
     * 创建管理员
     * @param adminAccountDto
     * @return
     */
    Boolean createAdmin(AuthAdminDto adminAccountDto);

    /**
     * 获取管理员信息
     * @param id
     * @return
     */
    AuthAdminDto getAdminDetail(String id);

    /**
     * 更新管理员
     * @param adminDto
     * @return
     */
    Boolean editAdmin(AuthAdminDto adminDto);

    /**
     * 分页
     * @param pageParam
     * @return
     */
    IPage<AuthAdminDto> pageQuery(PageParam<AuthAdminQueryDto> pageParam);

    /**
     * 修改密码
     * @param adminModify
     * @param accessToken
     * @return
     */
    boolean updatePassWord(AdminModifyDTO adminModify, String accessToken);

    /**
     * 重置密码
     * @param resetModify
     * @return
     */
    Result resetPassWord(ResetModifyDTO resetModify);
}
