package com.longmaster.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.admin.dto.auth.*;
import com.longmaster.admin.entity.AuthAdmin;
import com.longmaster.admin.entity.AuthAdminRole;
import com.longmaster.admin.entity.AuthRoles;
import com.longmaster.admin.mapper.AuthAdminMapper;
import com.longmaster.admin.mapper.AuthAdminRoleMapper;
import com.longmaster.admin.service.AuthAdminRoleService;
import com.longmaster.admin.service.AuthAdminService;
import com.longmaster.admin.service.AuthRolesService;
import com.longmaster.admin.service.MinioService;
import com.longmaster.core.bean.Token;
import com.longmaster.core.exception.AuthException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.util.AESUtil;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AuthAdminServiceImpl extends ServiceImpl<AuthAdminMapper, AuthAdmin> implements AuthAdminService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private AuthAdminRoleService adminRoleService;

    @Resource
    private AuthRolesService authRolesService;

    @Resource
    private AuthAdminRoleMapper authAdminRoleMapper;

    @Resource
    private MinioService minioService;

    @Value("${system.admin.tokenExpire:7200}")
    private Long tokenExpire;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public Token login(AuthDto account) {
        Assert.isTrue(StrUtil.isNotBlank(account.getAccount()), new ServiceException("??????????????????~"));
        Assert.isTrue(StrUtil.isNotBlank(account.getPassword()), new ServiceException("???????????????~"));

        String password = DigestUtils.md5DigestAsHex(AESUtil.desEncrypt(account.getPassword()).getBytes());
        AuthAdmin adminAccount = this.getOne(new LambdaQueryWrapper<AuthAdmin>()
                .eq(AuthAdmin::getAccount, account.getAccount())
                .eq(AuthAdmin::getPassword, password)
                .eq(AuthAdmin::getLogicDel, 0)
        );
        Assert.notNull(adminAccount, new AuthException("????????????????????????~"));
        Assert.isTrue(adminAccount.getStatus() == 0, new AuthException("?????????????????????????????????????????????????????????????????????"));

        //??????????????????
        List<AuthRoles> roles = adminRoleService.getAdminRoles(adminAccount.getId());

        AuthAdminDto accountDto = new AuthAdminDto();

        BeanUtils.copyProperties(adminAccount, accountDto);
        accountDto.setRoles(roles);
        accountDto.setPassword(null);

        String accessToken = Base64Utils.encodeToUrlSafeString(UUID.randomUUID().toString().getBytes());

        try {
            redisTemplate.opsForValue().set(accessToken, sObjectMapper.writeValueAsString(accountDto), tokenExpire, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Token.builder()
                .accessToken(accessToken)
                .expire(tokenExpire)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Override
    public Token loginBySmsCode(AuthSmsDto authData) {
        AuthAdmin adminAccount = this.getOne(new LambdaQueryWrapper<AuthAdmin>()
                .eq(AuthAdmin::getAccount, authData.getAccount())
                .eq(AuthAdmin::getLogicDel, 0)
        );
        Assert.notNull(adminAccount, new AuthException("????????????????????????~"));
        Assert.isTrue(adminAccount.getStatus() == 0, new AuthException("?????????????????????????????????????????????????????????????????????"));

        //??????????????????
        List<AuthRoles> roles = adminRoleService.getAdminRoles(adminAccount.getId());

        AuthAdminDto accountDto = new AuthAdminDto();

        BeanUtils.copyProperties(adminAccount, accountDto);
        accountDto.setRoles(roles);
        accountDto.setPassword(null);

        String accessToken = Base64Utils.encodeToUrlSafeString(UUID.randomUUID().toString().getBytes());

        try {
            redisTemplate.opsForValue().set(accessToken, sObjectMapper.writeValueAsString(accountDto), tokenExpire, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Token.builder()
                .accessToken(accessToken)
                .expire(tokenExpire)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Override
    public boolean existAdmin(String account) {
        AuthAdmin adminAccount = this.getOne(new LambdaQueryWrapper<AuthAdmin>()
                .eq(AuthAdmin::getAccount, account)
                .eq(AuthAdmin::getLogicDel, 0)
        );

        if (adminAccount != null) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * ??????
     *
     * @param accessToken
     * @return
     */
    @Override
    public boolean logout(String accessToken) {
        if (redisTemplate.hasKey(accessToken)) {
            return redisTemplate.delete(accessToken);
        }
        return true;
    }

    /**
     * ?????????????????????
     *
     * @param accessToken
     * @return
     */
    @Override
    public AuthAdminDto getInfo(String accessToken) {
        AuthAdminDto adminAccount = null;
        try {
            adminAccount = sObjectMapper.readValue((String) redisTemplate.opsForValue().get(accessToken), AuthAdminDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (!StrUtil.isEmpty(adminAccount.getAvatar())) {
            adminAccount.setAvatar(minioService.buildMinIoURL(adminAccount.getAvatar()));
        }
        return adminAccount;
    }


    /**
     * ???????????????
     *
     * @param adminAccount
     * @return
     */
    @Override
    public AuthAdmin register(AuthAdminDto adminAccount) {
        Assert.isTrue(StrUtil.isNotBlank(adminAccount.getAccount()), new ServiceException("????????????????????????~"));
        Assert.isTrue(StrUtil.isNotBlank(adminAccount.getPassword()), new ServiceException("???????????????~"));

        List<AuthAdmin> list = this.list(new LambdaQueryWrapper<AuthAdmin>()
                .eq(AuthAdmin::getAccount, adminAccount.getAccount())
        );
        Assert.isTrue(CollectionUtil.isEmpty(list), new ServiceException("??????%s??????????????????????????????~", adminAccount.getAccount()));

        AuthAdmin saveData = new AuthAdmin();
        BeanUtils.copyProperties(adminAccount, saveData);
        String password = DigestUtils.md5DigestAsHex(AESUtil.desEncrypt(adminAccount.getPassword()).getBytes());
        saveData.setPassword(password);
        saveData.setAvatar(minioService.getURI(saveData.getAvatar())); // ?????????
        this.save(saveData);

        return saveData;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean createAdmin(AuthAdminDto adminAccountDto) {
        Assert.isTrue(CollectionUtil.isNotEmpty(adminAccountDto.getRoleIds()), new IllegalArgumentException("????????????????????????????????????????????????"));
        log.info("[createAdmin] doing register admin info...");
        //register  admin info
        AuthAdmin account = this.register(adminAccountDto);

        log.info("[createAdmin] doing assign admin `{}` role info...", account.getId());
        adminAccountDto.getRoleIds().forEach(roleId -> {
            AuthAdminRole adminRole = new AuthAdminRole();
            adminRole.setAdminId(account.getId());
            adminRole.setRoleId(roleId);
            adminRoleService.save(adminRole);
        });
        authRolesService.reload();
        log.info("[createAdmin] create admin `{}` info successful, create flow finished!", account.getId());
        return true;
    }


    @Override
    public AuthAdminDto getAdminDetail(String id) {
        Assert.isTrue(StrUtil.isNotBlank(id), new ServiceException("??????id???????????????"));
        AuthAdminDto detail = baseMapper.getAdminDetail(id);
        detail.setAvatar(minioService.buildMinIoURL(detail.getAvatar()));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean editAdmin(AuthAdminDto adminDto) {
        Assert.isTrue(CollectionUtil.isNotEmpty(adminDto.getRoleIds()), new ServiceException("??????????????????????????????"));
        adminDto.setAvatar(minioService.getURI(adminDto.getAvatar()));

        String password = adminDto.getPassword();
        if (!StrUtil.isEmpty(password)) {
            adminDto.setPassword(DigestUtils.md5DigestAsHex(AESUtil.desEncrypt(password).getBytes()));
        }
        this.updateById(adminDto);
        //????????????????????????
        adminRoleService.remove(new LambdaQueryWrapper<AuthAdminRole>().eq(AuthAdminRole::getAdminId, adminDto.getId()));
        //????????????
        adminDto.getRoleIds().forEach(roleId -> {
            AuthAdminRole adminRole = new AuthAdminRole();
            adminRole.setRoleId(roleId);
            adminRole.setAdminId(adminDto.getId());
            adminRoleService.save(adminRole);
        });
        authRolesService.reload();
        log.info("[editAdmin] edit admin role is finish???");
        return true;
    }

    @Override
    public IPage<AuthAdminDto> pageQuery(PageParam<AuthAdminQueryDto> pageParam) {
        IPage<AuthAdminQueryDto> page = pageParam.getPage();
        AuthAdminQueryDto params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("???????????????????????????"));
        IPage<AuthAdminDto> iPage = baseMapper.pageSelect(page, params);
        iPage.getRecords().forEach(item -> {
            Long adminId = item.getId();
            List<AuthRoles> authRoles = authAdminRoleMapper.selectAdminRoles(adminId);
            item.setRoleIds(authRoles.stream().map(AuthRoles::getId).collect(Collectors.toList()));
            if (!StrUtil.isEmpty(item.getAvatar())) {
                item.setAvatar(minioService.buildMinIoURL(item.getAvatar()));
            }
        });
        return iPage;
    }

    @Override
    public boolean updatePassWord(AdminModifyDTO adminModify, String accessToken) {
        AuthAdminDto adminDto = this.getInfo(accessToken);
        Assert.isTrue(adminDto.getId().equals(adminModify.getId()), new ServiceException("??????????????????????????????"));

        // ??????
        String oldPassword = adminModify.getOldPassword();
        String password = adminModify.getPassword();
        String passwordAgain = adminModify.getPasswordAgain();

        AuthAdmin authAdmin = this.getOne(new LambdaQueryWrapper<AuthAdmin>().eq(AuthAdmin::getId, adminModify.getId()).select(AuthAdmin::getPassword));
        Assert.notNull(authAdmin, new ServiceException("??????????????????"));

        Assert.isTrue(DigestUtils.md5DigestAsHex(oldPassword.getBytes()).equals(authAdmin.getPassword()), new ServiceException("?????????????????????"));
        Assert.isTrue(password.equals(passwordAgain), new ServiceException("???????????????????????????~"));

        AuthAdmin entity = new AuthAdmin();
        entity.setId(adminModify.getId());
        entity.setPassword(DigestUtils.md5DigestAsHex(passwordAgain.getBytes()));

        return this.updateById(entity);
    }

    @Override
    public Result resetPassWord(ResetModifyDTO resetModify) {
        String account = resetModify.getAccount();
        String password = AESUtil.desEncrypt(resetModify.getPassword());
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        AuthAdmin authAdmin = this.getOne(new LambdaQueryWrapper<AuthAdmin>().eq(AuthAdmin::getAccount, account).eq(AuthAdmin::getStatus, 0));

        Assert.isFalse(password.equals(authAdmin.getPassword()), new ServiceException("????????????????????????????????????????????????????????????"));
        return Result.SUCCESS(this.update(new LambdaUpdateWrapper<AuthAdmin>().set(AuthAdmin::getPassword, password).eq(AuthAdmin::getAccount, account)));
    }
}
