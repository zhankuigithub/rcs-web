package com.longmaster.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.json.JSONObject;
import com.longmaster.admin.cache.GlobalCacheKeyDefine;
import com.longmaster.admin.dto.auth.*;
import com.longmaster.admin.service.AuthAdminService;
import com.longmaster.admin.service.RedisService;
import com.longmaster.core.bean.Token;
import com.longmaster.core.constant.AuthConstant;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.util.SmsTemplateUtil;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@Api(value = "LoginController", tags = "认证管理")
@RefreshScope
public class LoginController {

    @Resource
    private RedisService redisService;

    @Resource
    private AuthAdminService adminService;

    @Value("${sms.apiUrl}")
    private String smsApiUrl;

    @Value("${sms.app_id}")
    private String appId;

    @Value("${sms.client_id}")
    private String clientId;

    @Value("${sms.client_key}")
    private String clientKey;

    private static final Long CAPTCHA_CACHE_TIME_S = 180L;

    @ApiOperation(value = "登录", notes = "登录")
    @PostMapping("/login")
    public Result<Token> doLogin(HttpServletRequest request, @RequestBody @Validated AuthDto account) {
        String key = GlobalCacheKeyDefine.CAPTCHA_CACHE_KEY + request.getSession().getId();

        Assert.isTrue(redisService.isExist(key), new ServiceException("验证码已过期"));
        Assert.isTrue(account.getCode().equals(redisService.get(key)), new ServiceException("验证码错误！"));
        Token token = adminService.login(account);
        redisService.delete(key);
        return Result.SUCCESS("登录成功~", token);
    }

    @ApiOperation(value = "验证码", notes = "验证码")
    @GetMapping("/captcha/{timestamp}")
    public void getCode(@PathVariable Long timestamp, HttpServletRequest request, HttpServletResponse response) {
        String key = GlobalCacheKeyDefine.CAPTCHA_CACHE_KEY + request.getSession().getId();

        Assert.isTrue(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timestamp) <= 300, new ServiceException("无效时间戳"));
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 60, 4, 4);
        log.info("code is {}", captcha.getCode());
        redisService.set(key, captcha.getCode(), CAPTCHA_CACHE_TIME_S);
        try (ServletOutputStream outputStream = response.getOutputStream();) {
            captcha.write(outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @ApiOperation(value = "登出", notes = "登出")
    @PostMapping("/manage/logout")
    public Result logout(@RequestHeader(AuthConstant.TOKEN_KEY) String token) {
        adminService.logout(token);
        log.info("[logout] do method it's successful!...");
        return Result.SUCCESS("登出成功~");
    }

    @ApiOperation(value = "发送短信验证码", notes = "发送短信验证码")
    @PostMapping("/login/send/code")
    public Result sendSmsCode(HttpServletRequest request, @RequestBody AuthSmsDto authData) {
        String key = GlobalCacheKeyDefine.CAPTCHA_CACHE_KEY + request.getSession().getId();

        Assert.isTrue(redisService.isExist(key), new ServiceException("图型验证码已过期"));
        Assert.isTrue(authData.getCode().equals(redisService.get(key)), new ServiceException("请输入正确的图型验证码用于获取短信！"));

        if (authData.getAccount() == null || authData.getAccount() == "") {
            return Result.ERROR("必要参数不可为空");
        }
        if (!adminService.existAdmin(authData.getAccount())) {
            return Result.ERROR("管理员不存在或者已经被限制登录");
        }
        //随机一个四位数数字；
        Integer code = 1000 + (int) (Math.random() * (9999 - 1000 + 1));
        //保存在redis里
        redisService.set(GlobalCacheKeyDefine.CODE_CACHE + authData.getAccount(), code.toString(), CAPTCHA_CACHE_TIME_S);

        String apiUri = SmsTemplateUtil.getReqUri(smsApiUrl, appId, clientId, clientKey, authData.getAccount(), "1251");

        JSONObject smsInfo = new JSONObject();
        smsInfo.putOnce("client_id", Integer.parseInt(clientId));
        smsInfo.putOnce("app_id", Integer.parseInt(appId));
        smsInfo.putOnce("template_id", 1251);
        smsInfo.putOnce("mobile", authData.getAccount());
        smsInfo.putOnce("send_status", 1);

        JSONObject templateArgs = new JSONObject();
        templateArgs.putOnce("code", code.toString());
        templateArgs.putOnce("p1", "融合通信运营管理平台");
        templateArgs.putOnce("p2", "3分钟");

        smsInfo.putOnce("template_args", templateArgs);

        Response response = SmsTemplateUtil.apiRequest(apiUri, smsInfo.toString());

        return Result.SUCCESS("已发送", response == null ? null : response.toString());
    }

    @ApiOperation(value = "短信验证码登录 ", notes = "短信验证码登录")
    @PostMapping("login/by/smscode")
    public Result loginBySmsCode(@RequestBody @Validated AuthSmsDto authData) {
        String key = GlobalCacheKeyDefine.CODE_CACHE + authData.getAccount();
        Assert.isTrue(redisService.isExist(key), new ServiceException("验证码已过期"));
        Assert.isTrue(authData.getSmsCode().equals(redisService.get(key)), new ServiceException("验证码错误！"));

        Token token = adminService.loginBySmsCode(authData);
        redisService.delete(key);
        return Result.SUCCESS("登录成功~", token);
    }


    @GetMapping("/manage/info")
    @ApiOperation(value = "基本信息", notes = "获取当前登录人的基本信息")
    public Result info(@RequestHeader(AuthConstant.TOKEN_KEY) String token) {
        AuthAdminDto authAdminDto = adminService.getInfo(token);
        log.info("[info] do method it's successful!...");
        return Result.SUCCESS(authAdminDto);
    }

    @PostMapping("sendResetCode")
    @ApiOperation(value = "发送重置验证码", notes = "发送重置验证码不需要填写图形验证码，滑动验证即可")
    public Result sendResetCode(HttpServletRequest request, @RequestBody @Validated AccountDTO account) {

        if (!adminService.existAdmin(account.getAccount())) {
            return Result.ERROR("当前账户未在平台注册或已被冻结，请联系管理员~");
        }

        String randomCode = String.valueOf(1000 + (int) (Math.random() * (9999 - 1000 + 1)));
        redisService.set(GlobalCacheKeyDefine.CAPTCHA_CACHE_KEY_RESET_PWD + request.getSession().getId(), randomCode, CAPTCHA_CACHE_TIME_S);

        String apiUri = SmsTemplateUtil.getReqUri(smsApiUrl, appId, clientId, clientKey, account.getAccount(), "1251");

        JSONObject smsInfo = new JSONObject();
        smsInfo.putOnce("client_id", Integer.parseInt(clientId));
        smsInfo.putOnce("app_id", Integer.parseInt(appId));
        smsInfo.putOnce("template_id", 1251);
        smsInfo.putOnce("mobile", account.getAccount());
        smsInfo.putOnce("send_status", 1);

        JSONObject templateArgs = new JSONObject();
        templateArgs.putOnce("code", randomCode);
        templateArgs.putOnce("p1", "融合通信运营管理平台");
        templateArgs.putOnce("p2", "3分钟");

        smsInfo.putOnce("template_args", templateArgs);

        Response response = SmsTemplateUtil.apiRequest(apiUri, smsInfo.toString());

        return Result.SUCCESS("已发送", response == null ? null : response.toString());
    }


    @ApiOperation(value = "重置密码")
    @PostMapping("resetPassWord")
    public Result resetPassWord(HttpServletRequest request, @RequestBody @Validated ResetModifyDTO resetModify) {
        String key = GlobalCacheKeyDefine.CAPTCHA_CACHE_KEY_RESET_PWD + request.getSession().getId();

        Assert.isTrue(redisService.isExist(key), new ServiceException("验证码已过期"));
        Assert.isTrue(resetModify.getCode().equals(redisService.get(key)), new ServiceException("验证码错误！"));

        return Result.SUCCESS(adminService.resetPassWord(resetModify));
    }


}
