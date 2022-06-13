package com.longmaster.guixjks.service;

import com.longmaster.core.vo.Result;

public interface ILmtxService {

    // 获取未读消息数
    Result getNeverReadMessage(String phone);


    // 互殴去优惠券数
    Result getVoucherList(String phone);


    // 构建html路径

    /**
     * 1 我的页面 ， 2图文问诊 ，3 视频问诊 ，4问诊记录 ，5 我的消息 ，6 家庭档案 7 问诊券详情
     *
     * @param phone
     * @param type
     * @return
     */
    String buildHtml(String phone, int type);
}
